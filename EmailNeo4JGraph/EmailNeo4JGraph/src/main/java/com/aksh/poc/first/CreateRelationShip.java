package com.aksh.poc.first;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.graphdb.index.UniqueFactory.UniqueNodeFactory;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.ConstraintType;

import com.aksh.poc.EntityNames;
import com.aksh.poc.RelationShipCreator;
import com.aksh.poc.first.EmailRelationShipDAO.RelationshipTypes;

public class CreateRelationShip implements EntityNames, RelationShipCreator {
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss Z");
	private String dbPath = DB_PATH;
	GraphDatabaseService graphDb;
	Label personLavel = DynamicLabel.label(LABEL_PERSON);
	Label emailLabel = DynamicLabel.label(LABEL_EMAIL);

	public void creatRelationShip(List<Map<String, String>> mapList) {
		try (Transaction tx = graphDb.beginTx()) {
			for (Map<String, String> map2 : mapList) {
				createRelationShip_(map2);
			}
			System.out.println(new Date() + "-Created");
			tx.success();
		}
	}

	public void creatRelationShip(Map<String, String> map) {
		try (Transaction tx = graphDb.beginTx()) {
			createRelationShip_(map);
			tx.success();
		}
	}
	private void createRelationShip_(Map<String, String> map) {

		Node fromPerson = getFromPerson(map);

		List<Node> toPersons = getToPerson(map);

		Node email = getEmail(map, emailLabel);

		createSendsEmailRelationship(map, fromPerson, email);
		for (Node toPerson : toPersons) {
			createEmailsToRelationShip(map, fromPerson, toPerson, email,"TO");
			createRecEmailRelationship(map, toPerson, email, "TO");	
		}
		

		List<Node> ccNodes = getCCPerson(map);
		for (Node ccNode : ccNodes) {
			createEmailsToRelationShip(map, fromPerson, ccNode, email,"CC");
			createRecEmailRelationship(map, ccNode, email, "CC");
		}

		List<Node> bccNodes = getBCCPerson(map);
		for (Node bccNode : bccNodes) {
			Relationship bcc=createEmailsToRelationShip(map, fromPerson, bccNode, email,"CC");
			bcc.setProperty(RELATIONSHIP_PROP_CC_BCC, true);
			createRecEmailRelationship(map, bccNode, email, "BCC");
		}
	}

	private void createSendsEmailRelationship(Map<String, String> map,
			Node fromPerson, Node email) {
		Relationship sendsEmail = fromPerson.createRelationshipTo(email,
				RelationshipTypes.SENDS_EMAIL);
//		sendsEmail.setProperty(RELATIONSHIP_PROP_SENTTO, toPerson.getId());
		sendsEmail.setProperty(RELATIONSHIP_PROP_DATE,
				getDate(map.get(CSV_KEY_DATE)));
	}
	
	private void createRecEmailRelationship(Map<String, String> map,
			Node recPerson,Node email, String ccBcc) {
		Relationship recEmail = recPerson.createRelationshipTo(email,
				RelationshipTypes.REC_EMAIL);
//		sendsEmail.setProperty(RELATIONSHIP_PROP_SENTTO, toPerson.getId());
		recEmail.setProperty(RELATIONSHIP_PROP_DATE,
				getDate(map.get(CSV_KEY_DATE)));
		recEmail.setProperty(RELATIONSHIP_PROP_CC_BCC, ccBcc);
	}

	private Relationship createEmailsToRelationShip(Map<String, String> map,
			Node fromPerson, Node toPerson, Node email,String ccBcc) {
		Relationship sendsTo = fromPerson.createRelationshipTo(toPerson,RelationshipTypes.EMAILS_TO);
		sendsTo.setProperty(RELATIONSHIP_PROP_MESSAGE_ID, email.getId());
		sendsTo.setProperty(RELATIONSHIP_PROP_CC_BCC,ccBcc);
		sendsTo.setProperty(RELATIONSHIP_PROP_DATE,
				getDate(map.get(CSV_KEY_DATE)));
		return sendsTo;
	}
	private synchronized Node getEmail(Map<String, String> map, Label emailLabel) {
		String messageId = (String) trimIfString(map
				.get(EMAIL_MESSAGE_ID_PROPERTY));
		Node email = emailUniqueFactory.getOrCreate(EMAIL_MESSAGE_ID_PROPERTY,
				messageId);
		email.setProperty(EMAIL_MESSAGE_ID_PROPERTY, messageId);
		email.setProperty(EMAIL_PATH_PROPERTY,
				trimIfString(map.get(CSV_KEY_PATH)));
		email.setProperty(EMAIL_FILE_PROPERTY,
				trimIfString(map.get(CSV_KEY_FILE)));
		email.setProperty(EMAIL_SUBJECT_PROPERTY,
				trimIfString(map.get(CSV_KEY_SUBJECT)));
		email.setProperty(EMAIL_TEXT_PROPERTY, trimIfString(map.get(CSV_KEY_TEXT)));
		email.setProperty(EMAIL_EMAIL_TEXT_PROPERTY, trimIfString(map.get(CSV_KEY_EMAIL_TEXT)));
		email.setProperty(EMAIL_DATE_PROPERTY,
				getDate(map.get(CSV_KEY_DATE)));
//		email.setProperty(CSV_KEY_FROM, trimIfString(map.get(CSV_KEY_FROM)));
//		email.setProperty(CSV_KEY_TO, trimIfString(map.get(CSV_KEY_TO)));
		graphDb.index().forNodes(LABEL_EMAIL)
				.add(email, EMAIL_MESSAGE_ID_PROPERTY, messageId);

		return email;
	}
	
	private List<Node> getToPerson(Map<String, String> map) {
		String toEmail = (String) trimIfString(map.get(CSV_KEY_TO));
		String toName = (String) trimIfString(map.get(CSV_KEY_TONAME));
		return getNodeListFromEmailNames(map, toEmail, toName);
//		return findOrCreatePerson(toEmail, toName, map);
	}
	private Node getFromPerson(Map<String, String> map) {
		String fromEmail = (String) trimIfString(map.get(CSV_KEY_FROM));
		String fromName = (String) trimIfString(map.get(CSV_KEY_FROMNAME));
		return findOrCreatePerson(fromEmail, fromName, map);
	}
	private List<Node> getCCPerson(Map<String, String> map) {
		String cc = (String) trimIfString(map.get(CSV_KEY_CC));
		String ccName = (String) trimIfString(map.get(CSV_KEY_CCNAME));
		return getNodeListFromEmailNames(map, cc, ccName);
	}

	private List<Node> getBCCPerson(Map<String, String> map) {
		String cc = (String) trimIfString(map.get(CSV_KEY_BCC));
		String ccName = (String) trimIfString(map.get(CSV_KEY_BCCNAME));
		return getNodeListFromEmailNames(map, cc, ccName);
	}

	private List<Node> getNodeListFromEmailNames(Map<String, String> map,
			String cc, String ccName) {
		List<String> ccNames = getNameListFromCCNames(ccName);
		List<String> ccEmais = getCCEmailList(cc);
		List<Node> ccNodes = new ArrayList<Node>();
		if (ccEmais.size() == ccNames.size()) {
			for (int idx = 0; idx < ccEmais.size(); idx++) {
				ccNodes.add(findOrCreatePerson(ccEmais.get(idx),
						ccNames.get(idx), map));
			}
		}else{
			for (int idx = 0; idx < ccEmais.size(); idx++) {
				ccNodes.add(findOrCreatePerson(ccEmais.get(idx),
						"", map));
			}
			
		}
		return ccNodes;
	}
	private synchronized Node findOrCreatePerson(String toEmail, String name,
			Map<String, String> map) {
		Node person2 = personUniqueFactory.getOrCreate(PERSON_EMAILID_PROPERTY,
				toEmail);
		person2.setProperty(PERSON_EMAILID_PROPERTY, toEmail);
		person2.setProperty(PERSON_ISINTERNAL_PROPERTY,
				toEmail.endsWith("@enron.com"));
		graphDb.index().forNodes(LABEL_PERSON)
				.add(person2, PERSON_EMAILID_PROPERTY, toEmail);
		return person2;
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

	private void shutDown() {
		graphDb.shutdown();
	}
	private void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutDown();
			}
		});
	}

	public void init() {
		this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		registerShutdownHook(graphDb);
		createUniqueIndex();
		createUniqueFactory();
	}
	private void createUniqueIndex() {
		try (Transaction tx = graphDb.beginTx()) {
			createUniqueIndexFor(LABEL_PERSON, PERSON_EMAILID_PROPERTY);

			createUniqueIndexFor(LABEL_EMAIL, EMAIL_MESSAGE_ID_PROPERTY);

			tx.success();

		}

	}

	private void createUniqueIndexFor(String label, String key) {
		Iterable<ConstraintDefinition> personConstraints = this.graphDb
				.schema().getConstraints(DynamicLabel.label(label));
		boolean createPersonUnique = true;
		for (ConstraintDefinition constraintDefinition : personConstraints) {
			if (constraintDefinition
					.isConstraintType(ConstraintType.UNIQUENESS)) {
				createPersonUnique = false;
			}
		}
		if (createPersonUnique) {
			this.graphDb.schema().constraintFor(DynamicLabel.label(label))
					.assertPropertyIsUnique(key).create();
		}
	}

	private void createUniqueFactory() {
		try (Transaction tx = graphDb.beginTx()) {

			personUniqueFactory = createUniqueFactory(LABEL_PERSON,
					PERSON_EMAILID_PROPERTY);

			emailUniqueFactory = createUniqueFactory(LABEL_EMAIL,
					EMAIL_MESSAGE_ID_PROPERTY);
			
			emailContentUniqueFactory= createUniqueFactory(LABEL_EMAIL_CONTENT,
					EMAIL_MESSAGE_ID_PROPERTY);

			tx.success();

		}

	}

	private UniqueNodeFactory createUniqueFactory(final String label,
			final String uniqueProperty) {
		return new UniqueFactory.UniqueNodeFactory(graphDb, label) {
			@Override
			protected void initialize(Node created,
					Map<String, Object> properties) {
				created.addLabel(DynamicLabel.label(label));
				created.setProperty(uniqueProperty,
						properties.get(uniqueProperty));
			}
		};
	}
	UniqueFactory.UniqueNodeFactory personUniqueFactory = null;
	UniqueFactory.UniqueNodeFactory emailUniqueFactory = null;
	UniqueFactory.UniqueNodeFactory emailContentUniqueFactory = null;
	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	private String getDate(String dateStr) {
		/*Date date = null;
		try {
			date = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;*/
		return dateStr;
	}

	private String cleanName(String name) {
		return name.replaceAll(",", "");
	}

	private List<String> getNameListFromCCNames(String ccNames) {
		List<String> nameListRes = new ArrayList<String>();
		ccNames = ccNames != null ? ccNames : "";
		String[] str = ccNames.split("<*>");
		List<String> nameList = Arrays.asList(str);
		for (String name : nameList) {
			if(name.length()>0 && name.contains("<")){
				nameListRes.add(name.substring(0, name.indexOf("<")).replaceAll(
						",", ""));	
			}else if (name.length()>0){
				nameListRes.add(name.replaceAll(",", ""));
			}
			
		}
		return nameListRes;
	}
	private List<String> getCCEmailList(String ccNames) {
		ccNames = ccNames != null ? ccNames : "";
		return Arrays.asList(ccNames.split(","));
	}

}
