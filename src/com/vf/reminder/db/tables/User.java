package com.vf.reminder.db.tables;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "User")
public class User {
	private String mobile;
	private long time;
	private String regId;
	private String arn;
	private String country;
	private List<Friends> lstFriends;


	@DynamoDBHashKey(attributeName = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@DynamoDBAttribute(attributeName = "time")
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}


	@DynamoDBAttribute(attributeName = "arn")
	public String getArn() {
		return arn;
	}

	public void setArn(String arn) {
		this.arn = arn;
	}

	@DynamoDBAttribute(attributeName = "country")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@DynamoDBAttribute(attributeName = "regId")
	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}
	@DynamoDBAttribute
	@DynamoDBMarshalling(marshallerClass = FriendMarshaller.class)
	public List<Friends> getLstFriends() {
		return lstFriends;
	}

	public void setLstFriends(List<Friends> lstFriends) {
		this.lstFriends = lstFriends;
	}

	@Override
	public String toString() {
		return "User [mobile=" + mobile + ", time=" + time + ", regId=" + regId
				+ ", arn=" + arn + ", country=" + country + ", lstFriends="
				+ lstFriends + "]";
	}

	
	
}
