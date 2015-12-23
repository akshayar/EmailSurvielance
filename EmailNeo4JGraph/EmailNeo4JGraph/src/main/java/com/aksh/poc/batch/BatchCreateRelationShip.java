package com.aksh.poc.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import com.aksh.poc.EntityNames;
import com.aksh.poc.RelationShipCreator;

public class BatchCreateRelationShip
		implements
			EntityNames,
			RelationShipCreator {

	BatchInserter batchInserter = null;
	private String dbPath = DB_PATH;
	private int totalRecs=0;

	private void initDB() {
		try {
			start();
			Label personLabel = DynamicLabel.label(LABEL_PERSON);
//			batchInserter.createDeferredSchemaIndex(personLabel)
//					.on(PERSON_EMAILID_PROPERTY).create();
			batchInserter.createDeferredConstraint(personLabel)
					.assertPropertyIsUnique(PERSON_EMAILID_PROPERTY).create();

			Label emailLabel = DynamicLabel.label(LABEL_EMAIL);
//			batchInserter.createDeferredSchemaIndex(emailLabel)
//					.on(EMAIL_MESSAGE_ID_PROPERTY).create();
			batchInserter.createDeferredConstraint(emailLabel)
					.assertPropertyIsUnique(EMAIL_MESSAGE_ID_PROPERTY).create();	
		} finally{
			shutDown();
		}
		
	}

	private void start(){
		batchInserter = BatchInserters.inserter(dbPath);
		
	}
	public void creatRelationShip(List<Map<String, String>> mapList) {
		try {
			start();
			for (Map<String, String> map2 : mapList) {
				createRelationShip_(map2);
			}
			totalRecs+=mapList.size();
			System.out.println(new Date() + "-Total Created -"+totalRecs);
		} finally {
			shutDown();

		}
	}

	public void shutDown() {
		if (batchInserter != null) {
			batchInserter.shutdown();
		}

	}

	private void createRelationShip_(Map<String, String> map) {
		Label personLavel = DynamicLabel.label(LABEL_PERSON);
		Label emailLabel = DynamicLabel.label(LABEL_EMAIL);

		long fromPerson = getFromPerson(map, personLavel);

		long toPerson = getToPerson(map, personLavel);

		long email = getEmail(map, emailLabel);
		RelationshipType RELATIONSHIP_SENDS_EMAIL = DynamicRelationshipType
				.withName(RELATIONSHIP_LABEL_SENDS_EMAIL);
		Map<String, Object> sendsMail = new HashMap<String, Object>();
		sendsMail.put("sentTo", toPerson);
		RelationshipType RELATIONSHIP_EMAILS_TO = DynamicRelationshipType
				.withName(RELATIONSHIP_LABEL_EMAILS_TO);
		Map<String, Object> emailsTo = new HashMap<String, Object>();
		emailsTo.put(RELATIONSHIP_PROP_MESSAGE_ID, email);
		batchInserter.createRelationship(fromPerson, toPerson,
				RELATIONSHIP_EMAILS_TO, emailsTo);
		batchInserter.createRelationship(fromPerson, email,
				RELATIONSHIP_SENDS_EMAIL, sendsMail);
	}
	private long getEmail(Map<String, String> map, Label emailLabel) {
		Map<String, Object> properties = new HashMap<String, Object>();
		String messageId = (String) trimIfString(map
				.get(EMAIL_MESSAGE_ID_PROPERTY));
		properties.put(EMAIL_MESSAGE_ID_PROPERTY, messageId);
		properties.put(EMAIL_PATH_PROPERTY,
				trimIfString(map.get("metadata_path")));
		properties.put(EMAIL_FILE_PROPERTY,
				trimIfString(map.get("metadata_file")));
		properties
				.put(EMAIL_SUBJECT_PROPERTY, trimIfString(map.get("subject")));
		properties.put(EMAIL_TEXT_PROPERTY, trimIfString(map.get("text")));

		return batchInserter.createNode(properties,emailLabel);
	}
	private long getToPerson(Map<String, String> map, Label personLabel) {
		String toEmail = (String) trimIfString(map.get("To"));
		return createPerson(toEmail, personLabel);
	}
	private long getFromPerson(Map<String, String> map, Label personLabel) {
		String fromEmail = (String) trimIfString(map.get("From"));
		return createPerson(fromEmail, personLabel);
	}
	private long createPerson(String toEmail, Label personLabel) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PERSON_EMAILID_PROPERTY, toEmail);
		properties.put(PERSON_ISINTERNAL_PROPERTY,
				toEmail.endsWith("@enron.com"));
		return batchInserter.createNode(properties,personLabel);

	}

	private Object trimIfString(String obj) {
		Object result = obj;
		if (obj != null) {
			result = ((String) obj).trim();
		} else {
			result = "";
		}
		return result;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

}
