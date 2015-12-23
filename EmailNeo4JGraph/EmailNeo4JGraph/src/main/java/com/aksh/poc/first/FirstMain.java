package com.aksh.poc.first;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FirstMain {
	GraphDatabaseService graphDb;
	Node firstNode;
	Node secondNode;
	Relationship relationship;
	private static final String DB_PATH = "C:\\Users\\arawa3\\Documents\\Neo4j\\poc";
	private String dbPath = DB_PATH;

	public void start() {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		registerShutdownHook(graphDb);
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
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"neo4j.xml");
		FirstMain firstMain=(FirstMain)applicationContext.getBean("firstMain");
//		firstMain.findOperation();


	}
	
	/*private void findOperation(){
		try ( Transaction tx = graphDb.beginTx() ){
			firstNode = graphDb.findNode(new Label, "message", "Hello, ");	
		}
		
		System.out.println(firstNode);
	}*/

	private void insertOperation(){
		try ( Transaction tx = graphDb.beginTx() )
		{
			firstNode = graphDb.createNode();
			firstNode.setProperty( "message", "Hello, " );
			secondNode = graphDb.createNode();
			secondNode.setProperty( "message", "World!" );

			relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
			relationship.setProperty( "message", "brave Neo4j " );
			
		    // Database operations go here
		    tx.success();
		}
	}
	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}
	private static enum RelTypes implements RelationshipType
	{
	    KNOWS
	}

}
