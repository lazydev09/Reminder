package com.vf.reminder.alarm;


import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.vf.reminder.db.RemDBObjHelper;
import com.vf.reminder.db.tables.Reminder;


public class AlarmHelper {

	final public static String ONE_TIME = "onetime";
	final public static String ALARAM_ID = "AID";
	final public static String MSG= "MSG";
	final public static String FROM= "FROM";
	
	public static void StartBootReceiver(Context context){
		ComponentName receiver = new ComponentName(context, BootReciever.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
		        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		        PackageManager.DONT_KILL_APP);
	}
	
	public static void StopBootReceiver(Context context){
		ComponentName receiver = new ComponentName(context, BootReciever.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
		        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		        PackageManager.DONT_KILL_APP);
	}
	
	
	 
	 
	 public void CancelAlarm(Context context, RemDBObjHelper remHelper)
	    {
	        Intent intent = new Intent(context, AlarmReciever.class);
	        PendingIntent sender = PendingIntent.getBroadcast(context, Long.valueOf(remHelper.getId()).intValue(), intent, 0);
	        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	        alarmManager.cancel(sender);
	    }
	 
	 public void CancelAlarm(Context context, Reminder remHelper)
	    {
	        Intent intent = new Intent(context, AlarmReciever.class);
	        PendingIntent sender = PendingIntent.getBroadcast(context, Long.valueOf(remHelper.getId()).intValue(), intent, 0);
	        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	        alarmManager.cancel(sender);
	    }

	    public void setOnetimeTimer(Context context,  RemDBObjHelper remHelper){
	        Log.d("AlarmManager", "setting intent data as Alarm ID"+remHelper.getId());
	        Long alarmId = new Long(remHelper.getId());
	        setOnetimeTimer(context, alarmId, remHelper.getTime(), remHelper.getMsg(),remHelper.getFrom());
	        
	    }
	    
	    
	    public void setOnetimeTimer(Context context,  Reminder remHelper){
	        Log.d("AlarmManager", "setting intent data as Alarm ID"+remHelper.getId());
	        Long alarmId = new Long(remHelper.getId());
	        setOnetimeTimer(context, alarmId, remHelper.getDate(), remHelper.getMessage(),remHelper.getFromUser());
	        
	    }
	    
	    public void setOnetimeTimer(Context context,  Long reqCode, Long time, String msg, String from){
		     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		        Intent intent = new Intent(context, AlarmReciever.class);
		        intent.putExtra(ONE_TIME, Boolean.TRUE);
		        intent.putExtra(ALARAM_ID, reqCode);
		        intent.putExtra(MSG, msg);
		        intent.putExtra(FROM, from);
		        
		        PendingIntent pi = PendingIntent.getBroadcast(context, reqCode.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		        Log.d("AlarmManager", "setting time at "+time + "current time"+new Date().getTime());
		        Log.d("AlarmManager", "will fire at "+new Date(time));
		        Toast.makeText(context, "Alaram will fire at"+new Date(time), Toast.LENGTH_LONG).show();
		        am.set(AlarmManager.RTC_WAKEUP,  time, pi);
		        
		    }
	    
	    public void setOnetimeTimer(Context context){
	    	 Long time = new Date().getTime();
	    	 time = time +10*1000;
	    	// Long time = new GregorianCalendar().getTimeInMillis()+2*1000;
	    	 
		     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		        Intent intent = new Intent(context, AlarmReciever.class);
		        intent.putExtra(ONE_TIME, Boolean.TRUE);
		        PendingIntent pi = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		        am.set(AlarmManager.RTC_WAKEUP,time  , pi);
		        Toast.makeText(context, "Alarm Scheduled at "+new Date(time), Toast.LENGTH_LONG).show();
		        
		    }
	
}

