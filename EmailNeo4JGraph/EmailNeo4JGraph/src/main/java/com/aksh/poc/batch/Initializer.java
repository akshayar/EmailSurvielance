package com.aksh.poc.batch;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.ConstraintType;

import com.aksh.poc.EntityNames;

public class Initializer implements EntityNames{
	GraphDatabaseService graphDb;
	
	public void init(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
		createUniqueIndex();
	}
	private void createUniqueIndex() {
		try (Transaction tx = graphDb.beginTx()) {
			createUniqueIndexFor(LABEL_PERSON,PERSON_EMAILID_PROPERTY);
			
			createUniqueIndexFor(LABEL_EMAIL,EMAIL_MESSAGE_ID_PROPERTY);

			tx.success();

		}

	}

	private void createUniqueIndexFor(String label,String key) {
		Iterable<ConstraintDefinition> personConstraints=this.graphDb.schema().getConstraints(DynamicLabel.label(label));
		boolean createPersonUnique=true;
		for (ConstraintDefinition constraintDefinition : personConstraints) {
			if(constraintDefinition.isConstraintType(ConstraintType.UNIQUENESS)){
				createPersonUnique=false;
			}
		}
		if(createPersonUnique){
			this.graphDb.schema()
			.constraintFor(DynamicLabel.label(label))
			.assertPropertyIsUnique(key).create();	
		}
	}

	public void setGraphDb(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}

}
