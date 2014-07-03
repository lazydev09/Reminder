package com.vf.reminder.utils;

import java.util.Calendar;

public enum DateTimeMenuEnum {

	Morning("M"),AfterNoon("A"),Evening("E"),Night("N"), Today("T"), Tommorow("TMR");
	
	private String val;
	
	DateTimeMenuEnum(String val){
		this.val = val;
	}
	
	
	public  static int getHourOfDay(String val){
		switch(DateTimeMenuEnum.valueOf(val)){
		case Morning : return 9;
		case AfterNoon : return 12;
		case Evening : return 18;
		case Night : return 21;
		default: return 0;
		}
	}
	
	public int getDate(){
		
		switch(this){
		case Today : return 9;
		case Tommorow : return 12;
		case Evening : return 18;
		case Night : return 21;
		default: return 0;
		}
		
	
	}
}
