package com.aksh.poc.first;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import com.aksh.poc.EntityNames;

public class CSVReader implements EntityNames {
	private List<String> fileList = null;
	Iterator<String> iterator = null;
	private CsvMapReader reader = null;
	String filePath = null;
	public void init() throws IOException {
		if (filePath != null) {
			init(filePath);
		} else {
			iterator = fileList.iterator();
			init(iterator.next());
		}

	}
	private void init(String filePath_) throws IOException {
		System.out.println(filePath_);
		if (filePath_ != null) {
			reader = new CsvMapReader(new FileReader(filePath_),
					CsvPreference.STANDARD_PREFERENCE);
			final String[] header = reader.getHeader(true);
		}
	}

	public Map<String, String> read() throws IOException {
		Map<String, String> map = reader.read(mapping);
		if (map == null && iterator != null && iterator.hasNext()) {
			init(iterator.next());
			map = reader.read(mapping);
		}
		return map;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public void setFileList(List<String> fileList) {
		this.fileList = fileList;
	}
	// String[] mapping=new
	// String[]{"text","label","metadata_file","metadata_path","metadata_date","messageId","From","To","Subject"};
	/*
	 * String[] mapping = new String[]{"text", "label", "metadata_file",
	 * "metadata_path", "metadata_date", "cc", "bcc", "toName", "bccName",
	 * "fromName", "messageId", "From", "To", "ccName", "Subject", "Date"};
	 */
	String[] mapping = new String[]{CSV_KEY_TEXT, CSV_KEY_LABEL, CSV_KEY_FILE,
			CSV_KEY_PATH, "metadata_date", CSV_KEY_BCCNAME, CSV_KEY_TONAME,
			CSV_KEY_CCNAME, CSV_KEY_DATE, CSV_KEY_SUBJECT, CSV_KEY_TO,
			CSV_KEY_BCC, CSV_KEY_FROMNAME, CSV_KEY_MESSAGEID, CSV_KEY_EMAIL_TEXT,CSV_KEY_CC, CSV_KEY_FROM};
}
