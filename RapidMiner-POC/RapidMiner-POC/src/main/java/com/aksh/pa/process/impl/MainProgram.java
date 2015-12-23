package com.aksh.pa.process.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aksh.pa.process.ProcessWrapper;

import common.Logger;

public class MainProgram {
	private static final Logger logger=Logger.getLogger(MainProgram.class);
	private static final int DEFAULT_SIZE=50;
	private int numberOfThreads=5;
	private int size=DEFAULT_SIZE;

	private static final String RESULT_DIR="D:\\other-projects\\analytics-nlp\\rapid-miner\\process\\out";
	private String resultDir=RESULT_DIR;
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"beans.xml");
		CSVReader reader = (CSVReader) applicationContext
				.getBean("csvReader");
		MainProgram main=(MainProgram)applicationContext.getBean("main");
		List<List<String>> partitionedFileList=main.getPartitionedFileList(reader);
		main.process(applicationContext, partitionedFileList);
		
		
	}

	private void process(final ApplicationContext applicationContext,
			List<List<String>> partitionedFileList) throws InterruptedException {
		ExecutorService executorService=Executors.newFixedThreadPool(numberOfThreads);
		int it=0;
		for (final List<String> list : partitionedFileList) {
			final int itt=it++;
			Runnable runnable=new Runnable() {
				
				@Override
				public void run() {
					Map<String, Object> paramMap=new HashMap<String, Object>();
					paramMap.put(ProcessWrapper.KEY_EMAIL_FILE_LIST, list);
					paramMap.put(ProcessWrapper.KEY_RESULT_FILE, resultDir+"\\result-"+(itt)+".csv");
					 logger.info(list);
					 ProcessWrapper processWrapper=null;
					synchronized (MainProgram.class) {
						processWrapper=(ProcessWrapper) applicationContext.getBean("process");
					} 
					
					processWrapper.setParameter(paramMap);
					processWrapper.preProcess();
					processWrapper.run();
					
				}
			};
			executorService.execute(runnable);;
			
		}
		executorService.shutdown();
		executorService.awaitTermination(100000, TimeUnit.MINUTES);
	}

	private  List<List<String>> getPartitionedFileList(CSVReader reader) {
		List<String> fileList=reader.getFilePaths();
		List<String> temp=new ArrayList<String>();
		List<List<String>> fileListList=new ArrayList<List<String>>();
		
		int PARTITION=fileList.size()/size;
		for(int i=0;i<PARTITION+1;i++){
			int toIndex=Math.min((i+1)*size, fileList.size());
			List<String> subliList=fileList.subList(i*size,toIndex);
			temp.addAll(subliList);
			fileListList.add(subliList);
		}
		
		Assert.assertEquals(fileList.size(), temp.size());
		Assert.assertEquals(fileList, temp);
		return fileListList;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}
	public void setResultDir(String resultDir) {
		this.resultDir = resultDir;
	}

}
