package com.vf.reminder.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vf.reminder.db.tables.Friends;
import com.vf.reminder.db.tables.User;

public class StateHelper {

	private static final String TAG = "StateHelper";

	public static final String PREFS_NAME = "com.vf.reminder";
	public static final String STAT = "STAT";
	public static final String USER = "USER";
	public static final String FRIENDS = "FRIENDS";
	public static final String MOBILE = "MOBILE";

	public static final int REGISTERED = 0;
	public static final int BLOCKED = 1;
	public static final int ACTIVE = 2;

	// HashMap keys
	public static final String CONTACT_LOOKUP_KEY = "CONTACT_LOOKUP_KEY";

	public enum State {
		REGISTERED, BLOCKED, ACTIVE;

	}

	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}

	public static int getUserStat(Context context) {
		SharedPreferences settings = getSharedPreferences(context);
		return settings.getInt(STAT, -1);
	}

	public static void setUserStat(Context context, int stat) {
		SharedPreferences prefs = getSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putInt(STAT, stat);
		editor.commit();
	}

	public static void setString(Context context, String key, String val) {
		SharedPreferences prefs = getSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString(key, val);
		editor.commit();
	}

	public static void setUser(Context context, User user) {
		SharedPreferences prefs = getSharedPreferences(context);
		Editor editor = prefs.edit();
		String json = new Gson().toJson(user);
		editor.putString(USER, json);
		editor.commit();
	}

	public static User getUser(Context context) {
		SharedPreferences settings = getSharedPreferences(context);
		String json = settings.getString(USER, "NA");
		if (json == "NA") {
			return null;
		}
		User user = new Gson().fromJson(json, User.class);

		return user;
	}

	public static void setHashMap(Context context, String key, HashMap value) {
		SharedPreferences prefs = getSharedPreferences(context);
		Editor editor = prefs.edit();
		String json = new Gson().toJson(value);
		editor.putString(key, json);
		editor.commit();
	}

	public static HashMap getHashMap(Context context, String key) {
		SharedPreferences settings = getSharedPreferences(context);
		String json = settings.getString(key, "NA");
		if (json == "NA") {
			return null;
		}
		return new Gson().fromJson(json, HashMap.class);
	}

	public static String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(context);
		String registrationId = prefs.getString(
				GCMUtils.PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		return registrationId;
	}

	static File profileDir = null;
	private static final String PROILE_IMG_FILE_NAME = "user.png"; 
	static Bitmap profileBitmap = null;
	
	public Bitmap getProfilePic(Context context){
		try{
			if(profileBitmap==null){
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			return BitmapFactory.decodeFile(getProfileDir(context)+PROILE_IMG_FILE_NAME, options);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		
		}
		return null;
	}
	
	public static void saveProfilePic(Context context, Bitmap bitmap) {
		try {		
				File file = new File(getProfileDir(context), PROILE_IMG_FILE_NAME);
				FileOutputStream fOut = new FileOutputStream(file);

				bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
				fOut.flush();
				fOut.close();
		}

		catch (IOException e) {
			// Error while creating file
			e.printStackTrace();

		}
	}
	
	private static File getProfileDir(Context context){
		if (profileDir == null) {
			return context.getDir("profile", Context.MODE_PRIVATE);
		}
		return profileDir;
	}

	
	public static void saveFriends(Context context, List<Friends> lstFriends) {
		SharedPreferences prefs = getSharedPreferences(context);
		String json = new Gson().toJson(lstFriends, new TypeToken<List<Friends>>(){}.getType());
		Editor editor = prefs.edit();
		editor.putString(FRIENDS, json);
		editor.commit();
	}
	
	public static List<Friends> getFriends(Context context) {
		SharedPreferences settings = getSharedPreferences(context);
		String json = settings.getString(FRIENDS, "NA");
		if (json == "NA") {
			return new ArrayList<Friends>();
		}
		List<Friends> lstFriends = new Gson().fromJson(json, new TypeToken<List<Friends>>(){}.getType());

		return lstFriends;
	}

}
