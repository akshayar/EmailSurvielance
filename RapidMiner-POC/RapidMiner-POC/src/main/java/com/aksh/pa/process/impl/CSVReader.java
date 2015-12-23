package com.aksh.pa.process.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
	private File file=null;
	private List<String> filePaths=new ArrayList<String>();
	
	public void read() throws Exception{
		BufferedReader reader=new BufferedReader(new FileReader(file));
		String line=null;
		while((line=reader.readLine())!=null){
			filePaths.add(line.split(",")[1]);
		}
		
	}
	public List<String> getFilePaths() {
		return filePaths;
	}
	
	public void setFile(File file) {
		this.file = file;
	}

}
