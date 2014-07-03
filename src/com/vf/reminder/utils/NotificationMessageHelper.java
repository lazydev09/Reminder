package com.vf.reminder.utils;

import com.vf.reminder.db.tables.Reminder;

public class NotificationMessageHelper {

	public static final String INTENT_REM ="REM";
	
	private String intent;
	
	private String msg;
	private String frm;
	private String rid;
	private String type;
	private String s3Key;
	private long time;
	
	public String getIntent() {
		return intent;
	}
	public void setIntent(String intent) {
		this.intent = intent;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getFrm() {
		return frm;
	}
	public void setFrm(String frm) {
		this.frm = frm;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getS3Key() {
		return s3Key;
	}
	public void setS3Key(String s3Key) {
		this.s3Key = s3Key;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	
	public Reminder buildReminder(){
		Reminder r = new Reminder();
		r.setDate(this.time);
		r.setMessage(this.msg);
		r.setFromUser(this.frm);
		r.setType(this.type);
		r.setFileLoc(this.s3Key);
		return r;
	}
	
	
	
}
