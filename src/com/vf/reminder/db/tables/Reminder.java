package com.vf.reminder.db.tables;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Reminder")
public class Reminder implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4437862277646727109L;
	private long id;
	private String toUser;
	private String fromUser;
	private String message;
	private long date;
	private long inputDate;
	private String status;
	private String type;
	private String fileLoc;
	private String remoteId;
	

	@DynamoDBHashKey(attributeName = "id")
	@DynamoDBAutoGeneratedKey
	public String getRemoteId() {
		return remoteId;
	}
	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}
	@DynamoDBIgnore
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	 @DynamoDBIndexRangeKey(attributeName="toUser", 
             globalSecondaryIndexName="id-toUser-index")
	@DynamoDBAttribute(attributeName = "toUser")
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	@DynamoDBIndexRangeKey(attributeName="fromUser", 
            globalSecondaryIndexName="id-fromUser-index")
	@DynamoDBAttribute(attributeName = "fromUser")
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	@DynamoDBAttribute(attributeName = "message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@DynamoDBAttribute(attributeName = "date")
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	@DynamoDBAttribute(attributeName = "inputDate")
	public long getInputDate() {
		return inputDate;
	}
	public void setInputDate(long inputDate) {
		this.inputDate = inputDate;
	}
	@DynamoDBAttribute(attributeName = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@DynamoDBAttribute(attributeName = "type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@DynamoDBAttribute(attributeName = "fileLoc")
	public String getFileLoc() {
		return fileLoc;
	}
	public void setFileLoc(String fileLoc) {
		this.fileLoc = fileLoc;
	}
	
	
}