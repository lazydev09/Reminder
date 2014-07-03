package com.vf.reminder.utils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMUtils {

	Context context;
	AtomicInteger msgId = new AtomicInteger();
	public GCMUtils(Context context){
		this.context = context;
	}
	
	 public static interface TaskCallbacks {
		    void onPreExecute();
		    void onProgressUpdate(int percent);
		    void onCancelled();
		    void onPostExecute();
		  }
	 
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "128436466251";
    static final String TAG = "GCMUtils";
    
    private TaskCallbacks mCallbacks;
	  private GCMRegTask mTask;
	  
	  GoogleCloudMessaging gcm;
	  String regid;
	  
	  
	  
	  public void register(){
		  gcm = GoogleCloudMessaging.getInstance(context);
          regid = getRegistrationId(context);

          if (regid.isEmpty()) {
          	registerInBackground();
              
          }
	  }
	  

	  
	  
	  
	   private void registerInBackground() {
	        new AsyncTask<Void, Void, String>() {
	            @Override
	            protected String doInBackground(Void... params) {
	                String msg = "";
	                try {
	                    if (gcm == null) {
	                        gcm = GoogleCloudMessaging.getInstance(context);
	                    }
	                    regid = gcm.register(SENDER_ID);
	                    msg = "Device registered, registration ID=" + regid;
	                    Log.d(TAG, msg);

	                    // You should send the registration ID to your server over HTTP, so it
	                    // can use GCM/HTTP or CCS to send messages to your app.
	                    sendRegistrationIdToBackend();

	                    // For this demo: we don't need to send it because the device will send
	                    // upstream messages to a server that echo back the message using the
	                    // 'from' address in the message.

	                    // Persist the regID - no need to register again.
	                    storeRegistrationId(context, regid);
	                } catch (IOException ex) {
	                    msg = "Error :" + ex.getMessage();
	                    // If there is an error, don't just keep trying to register.
	                    // Require the user to click a button again, or perform
	                    // exponential back-off.
	                }
	                return msg;
	            }

	            @Override
	            protected void onPostExecute(String msg) {
	            	Log.i(TAG, "registerInBackground : onPostExecute "+msg);
	            }
	        }.execute(null, null, null);
	    }
	   
	   
	   /**
	     * Stores the registration ID and the app versionCode in the application's
	     * {@code SharedPreferences}.
	     *
	     * @param context application's context.
	     * @param regId registration ID
	     */
	    private void storeRegistrationId(Context context, String regId) {
	        final SharedPreferences prefs = getGcmPreferences(context);
	        int appVersion = getAppVersion(context);
	        Log.i(TAG, "Saving regId on app version " + appVersion);
	        SharedPreferences.Editor editor = prefs.edit();
	        editor.putString(PROPERTY_REG_ID, regId);
	        editor.putInt(PROPERTY_APP_VERSION, appVersion);
	        editor.commit();
	    }

	    /**
	     * Gets the current registration ID for application on GCM service, if there is one.
	     * <p>
	     * If result is empty, the app needs to register.
	     *
	     * @return registration ID, or empty string if there is no existing
	     *         registration ID.
	     */
	    private String getRegistrationId(Context context) {
	        final SharedPreferences prefs = getGcmPreferences(context);
	        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	        if (registrationId.isEmpty()) {
	            Log.i(TAG, "Registration not found.");
	            return "";
	        }
	        // Check if app was updated; if so, it must clear the registration ID
	        // since the existing regID is not guaranteed to work with the new
	        // app version.
	        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	        int currentVersion = getAppVersion(context);
	        if (registeredVersion != currentVersion) {
	            Log.i(TAG, "App version changed."+registrationId);
	            return "";
	        }
	        Log.i(TAG, "registrationId : "+registrationId);
	        return registrationId;
	    }
	    
	    
	    /**
	     * @return Application's version code from the {@code PackageManager}.
	     */
	    private static int getAppVersion(Context context) {
	        try {
	            PackageInfo packageInfo = context.getPackageManager()
	                    .getPackageInfo(context.getPackageName(), 0);
	            return packageInfo.versionCode;
	        } catch (NameNotFoundException e) {
	            // should never happen
	            throw new RuntimeException("Could not get package name: " + e);
	        }
	    }

	    /**
	     * @return Application's {@code SharedPreferences}.
	     */
	    private SharedPreferences getGcmPreferences(Context context) {
	        // This sample app persists the registration ID in shared preferences, but
	        // how you store the regID in your app is up to you.
	      //  return context.getSharedPreferences(GCMTaskFragment.class.getSimpleName(),Context.MODE_PRIVATE);
	    	return StateHelper.getSharedPreferences(context);
	    }
	    /**
	     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
	     * messages to your app. Not needed for this demo since the device sends upstream messages
	     * to a server that echoes back the message using the 'from' address in the message.
	     */
	    private void sendRegistrationIdToBackend() {
	      // Your implementation here.
	    }
	
		  private class GCMRegTask extends AsyncTask<Void, Integer, Void> {
			   @Override
			    protected Void doInBackground(Void... params) {
				   String msg = "";
	               try {
	              	 Log.i(TAG, "running in doInBackground.......");
	                   Bundle data = new Bundle();
	                   data.putString("my_message", "Hello World");
	                   data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
	                   String id = Integer.toString(msgId.incrementAndGet());
	                   gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
	                   msg = "Sent message";
	               } catch (IOException ex) {
	                   msg = "Error :" + ex.getMessage();
	                   Log.e(TAG, "error in doInBackground......."+msg);
	               }
	               return null;
			   }
		  }
		  
}
