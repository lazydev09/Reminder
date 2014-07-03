package com.vf.reminder.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver{

	private static final String TAG = "SmsReceiver";
	 private SharedPreferences preferences;

	    @Override
	    public void onReceive(Context context, Intent intent) {
	        

	        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
	            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
	            SmsMessage[] msgs = null;
	            String msg_from;
	            if (bundle != null){
	                //---retrieve the SMS message received---
	                try{
	                    Object[] pdus = (Object[]) bundle.get("pdus");
	                    msgs = new SmsMessage[pdus.length];
	                    for(int i=0; i<msgs.length; i++){
	                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
	                        msg_from = msgs[i].getOriginatingAddress();
	                        Log.i(TAG, "msg_from : "+msg_from);
	                        String dispayAddress = msgs[i].getDisplayOriginatingAddress();
	                        Log.i(TAG, "dispayAddress : "+dispayAddress);
	                        String msgBody = msgs[i].getMessageBody();
	                        Log.i(TAG, "msgBody : "+msgBody);
	                    }
	                }catch(Exception e){
//	                            Log.d("Exception caught",e.getMessage());
	                }
	            }
	        }
	    }
}