package com.vf.reminder.db;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class RemDBObjHelper  implements Serializable{

	@Override
	public String toString() {
		return "RemDBObjHelper [id=" + id + ", type=" + type + ", msg=" + msg
				+ ", time=" + time + ", file_loc=" + file_loc + ", stat="
				+ stat + ", from=" + from + ", r_id=" + r_id + "]";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3253332419696443491L;
	public static final String INPUT = "I";
	public static final String SYNC = "S";
	public static final String SNOOGE = "S";
	public static final String DELETE = "D";
	
	private long id;
	private String type;
	private String msg;
	private Long time;
	private String file_loc;
	private String stat;
	private String from;
	private String r_id;
	
	
	public long getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public String getMsg() {
		return msg;
	}
	public Long getTime() {
		return time;
	}
	public String getFile_loc() {
		return file_loc;
	}
	public String getStat() {
		return stat;
	}
	public String getR_id() {
		return r_id;
	}
	
	public String getFrom(){
		return from;
	}
	
	
	
	private RemDBObjHelper(Builder builder){
		
		id = builder.id;
		type = builder.type;
		msg = builder.msg;
		time = builder.time;
		file_loc = builder.file_loc;
		if(file_loc==null || file_loc.isEmpty()){
			file_loc = "NA";
		}
		stat = RemDBObjHelper.INPUT;
		r_id = builder.r_id;
		if(r_id==null || r_id.isEmpty()){
			r_id = "TODO";
		}
		from = builder.from;
	}
	
	public void  setId(long id){
		this.id = id;
	}
	
	public void setRemoteId(String id){
		this.r_id = id;
	}
	
	public void setTime(Long time){
		this.time = time;
	}
	
	public static final class Builder {
		
		private int id;
		private String type;
		private String msg;
		private Long time;
		private String file_loc;
		private String stat;
		private String from;
		private String r_id;
		private Calendar cal = Calendar.getInstance();
		
		{
			cal.setTimeInMillis(System.currentTimeMillis());
		}
		
		public Builder localId(int value){
			id = value;
			return this;
		}
		public Builder type(String value){
			type = value;
			return this;
		}
		
		public Builder message(String value){
			msg = value;
			return this;
		}
		public Builder time(){
			time = cal.getTimeInMillis();
			Log.d("BUILDER",new Date(time) + "current sytem time" +new Date(System.currentTimeMillis()));
			return this;
		}
		
		public Builder fileLocation(String value){
			file_loc = value;
			return this;
		}
		
		public Builder status(String value){
			stat = value;
			return this;
		}
		
		
		public Builder remote_id(String value){
			r_id = value;
			return this;
		}
		
		public Builder fromUser(String user ){
			from = user;
			return this;
		}
		
		public Builder addToday(){
			//default date set in init
			return this;
		}
		
		public Builder addTomorrow(){
			 cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
			 return this;
		}
		
		public Builder addDate(int date, int month, int year){
			 cal.set(Calendar.DATE, date);
			 cal.set(Calendar.MONTH, month);
			 cal.set(Calendar.YEAR, year);
			 return this;
		}
		
		//time methods
		public Builder addTime(int hour, int min){
				 cal.set(Calendar.HOUR_OF_DAY, hour);
				 cal.set(Calendar.MINUTE, min);
				 Log.d("BUILDER", "setting time in addTime "+hour +": "+min +" final date "+new Date(cal.getTimeInMillis()));
				 return this;
					
		}
		
		
		public void addMorning(){
			addTime(9, 0);
		}
		
		public void addAfterNoon(){
			addTime(12, 0);
		}
		
		public void addEvening(){
			addTime(18, 0);
		}
		public void addNight(){
			Log.d("BUILDER", "addNight called");
			addTime(21, 0);
		}
		
		public void addCurrentTime(){
			Calendar currentTime = Calendar.getInstance();
			currentTime.setTimeInMillis(System.currentTimeMillis());
			addTime(currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE));
		}
		
		
	
		public String getType(){
			return this.type;
		}
		
	
		
		 public RemDBObjHelper build() {
		      return new RemDBObjHelper(this);
		    }
		 
		
	}
	
	
	
}
