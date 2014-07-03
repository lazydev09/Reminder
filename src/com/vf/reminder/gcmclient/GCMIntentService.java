package com.vf.reminder.gcmclient;

import java.util.Date;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.vf.reminder.MainActivity;
import com.vf.reminder.R;
import com.vf.reminder.alarm.AlarmHelper;
import com.vf.reminder.db.ReminderDBHelper;
import com.vf.reminder.db.tables.Reminder;
import com.vf.reminder.utils.NotificationMessageHelper;
import com.vf.reminder.utils.Utils;

public class GCMIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("GCMIntentService",
				"****** MESSAGE RECIEVED IN onHandleIntent ******");
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				//sendNotification("Received: " + extras.toString());
				try{
					Log.i(TAG, "Received: " + extras.toString());
					String gcmMessage = extras.getString("message");
					Log.i(TAG, "gcmMessage: " + gcmMessage);
					  StringBuilder payload = new StringBuilder();
					
					NotificationMessageHelper nMsg = new Gson().fromJson(
							gcmMessage, NotificationMessageHelper.class);

					if (NotificationMessageHelper.INTENT_REM.equalsIgnoreCase(nMsg.getIntent())) {
						// msg reminder
						Reminder r = nMsg.buildReminder();
						Log.i(TAG, "reminder: " + r.toString());
						ReminderDBHelper dbHelper = new ReminderDBHelper(getApplicationContext());
						long rowid = dbHelper.insertReminder(r);
						 AlarmHelper alarm = new AlarmHelper();
						 alarm.setOnetimeTimer(getApplicationContext(), rowid,r.getDate(),r.getMessage(),r.getFromUser());
						Utils.debug(TAG, "Alaram set ****** at "+new Date(r.getDate()));
					}// reminder msg

				
					
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("GCM Notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);
		// mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
		mBuilder.setLights(Color.GREEN, 3000, 3000);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

	}

	private void saveAsAlarm() {

	}

}
