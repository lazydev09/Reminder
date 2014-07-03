package com.vf.reminder.db.tables;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "UserReg")
public class UserReg {
	private String urid;
	private String mobile;
	private String email;//facebook or emails
	private String country;
	private String city;
	private String sex;
	private String ip;
	private String geoLoc;
	private String regId;
	
	@DynamoDBAttribute(attributeName = "urid")
	public String getUrid() {
		return urid;
	}
	public void setUrid(String id) {
		this.urid = id;
	}
	@DynamoDBAttribute(attributeName = "mobile")
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@DynamoDBAttribute(attributeName = "email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@DynamoDBAttribute(attributeName = "country")
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	@DynamoDBAttribute(attributeName = "city")
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	@DynamoDBAttribute(attributeName = "sex")
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	@DynamoDBAttribute(attributeName = "ip")
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	@DynamoDBAttribute(attributeName = "geoLoc")
	public String getGeoLoc() {
		return geoLoc;
	}
	public void setGeoLoc(String geoLoc) {
		this.geoLoc = geoLoc;
	}
	@DynamoDBAttribute(attributeName = "regId")
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	
	
}
