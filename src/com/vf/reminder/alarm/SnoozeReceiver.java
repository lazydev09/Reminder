package com.vf.reminder.alarm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SnoozeReceiver extends BroadcastReceiver {
	private NotificationManager mNotificationManager;
	private final int snooze_time = 1000 * 60 * 5 ;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d("SNOOZE", "insize snooze reciver ******");
		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		String msg = (String) intent.getExtras().get(AlarmHelper.MSG);
		String from = (String) intent.getExtras().get(AlarmHelper.FROM);
		Long aid =intent.getExtras().getLong(AlarmHelper.ALARAM_ID);
		
		Log.d("SNOOZE", "insize snooze reciver ****** aid : "+aid.intValue());
		mNotificationManager.cancel(aid.intValue());
		AlarmHelper alarm = new AlarmHelper();
		alarm.setOnetimeTimer(context, aid, System.currentTimeMillis()
				+ snooze_time, msg,from);
		Toast.makeText(context, "Szooze by 5 min", Toast.LENGTH_LONG).show();

	}

}

