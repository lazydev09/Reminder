package com.vf.reminder.alarm;


import java.util.Date;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

public class AlarmReciever extends WakefulBroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		Log.d("on alaram revicered", new Date().toString());
		Log.d("on alaram revicered", new Date().toString());
		Log.d("on alaram revicered", new Date().toString());
		Log.d("on alaram revicered", new Date().toString());
		Toast.makeText(context, "Alarm received in  AlarmReciever", Toast.LENGTH_LONG).show();
		
		ComponentName comp = new ComponentName(context.getPackageName(), AlarmIntentService.class.getName());
		startWakefulService(context, (arg1.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
		
	}
	
	

}
