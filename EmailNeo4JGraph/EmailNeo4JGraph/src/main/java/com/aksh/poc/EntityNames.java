package com.aksh.poc;

public interface EntityNames {
	public static final String LABEL_PERSON = "person";
	public static final String LABEL_EMAIL = "email";
	public static final String LABEL_EMAIL_CONTENT = "emailContent";
	public static final String PERSON_EMAILID_PROPERTY = "emailId";
	public static final String PERSON_ISINTERNAL_PROPERTY = "isInternal";
	public static final String EMAIL_TEXT_PROPERTY = "text";
	public static final String EMAIL_PATH_PROPERTY = "path";
	public static final String EMAIL_FILE_PROPERTY = "file";
	public static final String EMAIL_SUBJECT_PROPERTY = "subject";
	public static final String EMAIL_MESSAGE_ID_PROPERTY = "messageId";
	public static final String EMAIL_EMAIL_TEXT_PROPERTY = "emailText";
	
	
	public static final String CSV_KEY_TEXT=EMAIL_TEXT_PROPERTY;
	public static final String CSV_KEY_FILE="metadata_file";
	public static final String CSV_KEY_PATH="metadata_path";
	public static final String CSV_KEY_CC="cc";
	public static final String CSV_KEY_BCC="bcc";
	public static final String CSV_KEY_TONAME="toName";
	public static final String CSV_KEY_FROMNAME="fromName";
	public static final String CSV_KEY_CCNAME="ccName";
	public static final String CSV_KEY_BCCNAME="bccName";
	public static final String CSV_KEY_MESSAGEID="messageId";
	public static final String CSV_KEY_FROM="From";
	public static final String CSV_KEY_TO="To";
	public static final String CSV_KEY_SUBJECT="Subject";
	public static final String CSV_KEY_DATE="Date";
	public static final String CSV_KEY_LABEL="label";
	public static final String CSV_KEY_EMAIL_TEXT="emailText";
	
	public static final String RELATIONSHIP_PROP_SENTTO="sentTo";
	public static final String RELATIONSHIP_PROP_DATE=CSV_KEY_DATE;
	public static final String RELATIONSHIP_PROP_MESSAGE_ID=EMAIL_MESSAGE_ID_PROPERTY;
	public static final String RELATIONSHIP_PROP_CC_BCC="ccBcc";
	
	public static final String RELATIONSHIP_LABEL_SENDS_EMAIL="SENDS_EMAIL";
	public static final String RELATIONSHIP_LABEL_EMAILS_TO="EMAILS_TO";
	
	public static final String EMAIL_DATE_PROPERTY=CSV_KEY_DATE;
	
}
