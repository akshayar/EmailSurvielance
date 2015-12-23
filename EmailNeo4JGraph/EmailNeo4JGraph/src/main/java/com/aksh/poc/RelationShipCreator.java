package com.aksh.poc;

import java.util.List;
import java.util.Map;

public interface RelationShipCreator {
	final String DB_PATH = "C:\\Users\\arawa3\\Documents\\Neo4j\\enron-2";
	void creatRelationShip(List<Map<String, String>> mapList);

}
