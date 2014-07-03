package com.vf.reminder.aws.ddb;

public class DBResponseHelper<T> {

	private boolean isSuccess;
	private String errMessage;
	private T t;
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getErrMessage() {
		return errMessage;
	}
	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}
	public T getT() {
		return t;
	}
	public void setT(T t) {
		this.t = t;
	}
	
	
	
}
