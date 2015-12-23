package com.aksh.poc.first;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aksh.poc.RelationShipCreator;

public class EmailRelationShipDAO implements ApplicationContextAware {
	GraphDatabaseService graphDb;
	RelationShipCreator relationShipCreator = null;
	private List<String> fileList;
	ApplicationContext applicationContext = null;
	private String resultDir = null;

	enum RelationshipTypes implements RelationshipType {
		EMAILS_TO, SENDS_EMAIL, REC_EMAIL
	};

	public static void main(String[] args) throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"neo4j.xml");
		final EmailRelationShipDAO emailRelationShipDAO = (EmailRelationShipDAO) applicationContext
				.getBean("emailRelationShipDAO");
		ExecutorService executorService = Executors.newFixedThreadPool(1);

		List<String> tempFileList = emailRelationShipDAO.getFileList();
		for (final String string : tempFileList) {
//			emailRelationShipDAO.readAndSave(string);

			executorService.execute(new Runnable() {

				@Override
				public void run() {

					try {
						emailRelationShipDAO.readAndSave(string);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			 

		}
		executorService.shutdown();
		executorService.awaitTermination(100000, TimeUnit.MINUTES);

	}
	private void readAndSave(String string) throws IOException {
		CSVReader csvReader = (CSVReader) applicationContext
				.getBean("csvReader");
		csvReader.setFilePath(string);
		csvReader.init();
		Map<String, String> map = null;
		int idx = 0;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		while ((map = csvReader.read()) != null) {
			idx++;
			list.add(map);
			if (idx % 1000 == 0) {
				 relationShipCreator.creatRelationShip(list);
				 System.out.println("Saved");
			}else{
				continue;
			}
			list = new ArrayList<Map<String, String>>();
		}
		relationShipCreator.creatRelationShip(list);
	}

	public void setFileList(List<String> fileList) {
		this.fileList = fileList;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		this.applicationContext = arg0;

	}
	private List<String> getFileList() {
		List<String> result = fileList;
		if (resultDir != null) {
			File file = new File(resultDir);
			if (file.exists() && file.isDirectory()) {
				File[] csvFiles = file.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						name = name.toLowerCase();
						return name.startsWith("result")
								&& name.endsWith(".csv");
					}
				});
				List<File> fileListT = Arrays.asList(csvFiles);
				if (!fileListT.isEmpty()) {
					result = new ArrayList<String>();
					for (File file2 : fileListT) {
						result.add(file2.getAbsolutePath());
					}
				}
			}
		}
		return result;
	}

	public void setResultDir(String resultDir) {
		this.resultDir = resultDir;
	}
	public void setRelationShipCreator(RelationShipCreator relationShipCreator) {
		this.relationShipCreator = relationShipCreator;
	}
}
