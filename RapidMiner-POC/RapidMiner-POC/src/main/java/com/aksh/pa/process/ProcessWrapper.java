package com.aksh.pa.process;

import java.util.Map;

public interface ProcessWrapper {
	void loadProcess();
	void setParameter(Map<String, Object> parameters);
	void preProcess();
	void run();
	void processResults();
	
	public static final String KEY_EMAIL_FILE_LIST="emailFileList";
	public static final String KEY_RESULT_FILE="resultFile";
}
