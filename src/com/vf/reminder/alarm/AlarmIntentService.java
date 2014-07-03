package com.vf.reminder.alarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;

import com.vf.reminder.MainActivity;
import com.vf.reminder.R;
import com.vf.reminder.utils.QuickContactPhotoHelper;

public class AlarmIntentService extends IntentService {

	 private NotificationManager mNotificationManager;
	 NotificationCompat.Builder builder;
	 private final String TAG = "AlarmIntentService";   
	public AlarmIntentService() {
		super("AlarmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		
		String msg = (String)arg0.getExtras().get(AlarmHelper.MSG);
		String from = (String) arg0.getExtras().get(AlarmHelper.FROM);
		Long aid = arg0.getExtras().getLong(AlarmHelper.ALARAM_ID);
		
		Log.d(TAG,"inside onHandleItent" +aid);
		sendNotification(msg,from, aid);
		

	}
	
	

	
	 private void sendNotification(String msg, String from, Long notificationId) {
	        mNotificationManager = (NotificationManager)
	                this.getSystemService(Context.NOTIFICATION_SERVICE);

	        //set for dialog
	        Intent mainIntent =  new Intent(this, MainActivity.class);
	        mainIntent.putExtra("TRANSPARENT", false);
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	        		mainIntent, 0);
	      
	        
	        //snooze
	        //Intent emptyIntent = new Intent(this,SnoozeService.class);
	        Intent snoozeIntent = new Intent(this,SnoozeReceiver.class);
	        snoozeIntent.putExtra(AlarmHelper.ONE_TIME, Boolean.TRUE);
	        snoozeIntent.putExtra(AlarmHelper.ALARAM_ID, notificationId);
	        snoozeIntent.putExtra(AlarmHelper.MSG, msg);
	        snoozeIntent.putExtra(AlarmHelper.FROM, from);
	        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(getBaseContext(), 3, snoozeIntent, 0);
	       // PendingIntent snoozeIntent = PendingIntent.getService(getApplicationContext(), 5, emptyIntent, 0);
	        
	        
	      //  sendTransparentActivityBroadcast();
	        Bitmap bitmap = null;
	       try{
	    	   QuickContactPhotoHelper photoHelper = new QuickContactPhotoHelper(getApplicationContext());
	    	   ImageView img = new ImageView(getApplicationContext());
	    	    bitmap = photoHelper.getThumbnail(img, from);
	       }catch(Exception ex){
	    	   ex.printStackTrace();
	    	   //ignore and show lauch image
	       }
	        
	        NotificationCompat.Builder mBuilder =
	                new NotificationCompat.Builder(this)
	        
	        .setContentTitle("Reminder")
	        .setStyle(new NotificationCompat.BigTextStyle()
	        .bigText(msg))
	        .setContentText(msg);
	        mBuilder.addAction(R.drawable.clock, "Snooze", snoozePendingIntent);
	       if(bitmap!=null){

		        mBuilder.setLargeIcon(bitmap);   
	       }
	       
	       else{
	    	   mBuilder.setSmallIcon(R.drawable.ic_action_add);
	       }
	      //  mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
	        mBuilder.setLights(Color.GREEN, 3000, 3000);
	        mBuilder.setContentIntent(contentIntent);
	        Log.d(TAG, "notificationId set ******"+notificationId.intValue());
	        mNotificationManager.notify(notificationId.intValue(), mBuilder.build());
	    }


		private void sendTransparentActivityBroadcast(){
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction("com.vf.ACTION_TRANSPARENT");
			sendBroadcast(broadcastIntent);
		}
}

