package com.vf.reminder.utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.vf.reminder.R;
import com.vf.reminder.db.tables.Friends;
import com.vf.reminder.db.tables.User;

public class Utils {

	static boolean debug = true;
	private static final String TAG = "Utils";
	
	public static void debug(String tag, String msg){
		if(debug){
			Log.d(tag, msg);
		}
	}
	
	public static void toast(Context context, String msg){
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	//Reminder
	public static final String FORWARD = "FORWARD";
	public static final String TYPE = "TYPE";
	
	// GCM
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String SENDER_ID = "1069861711569";
	
	//reminder helper

	public static final String AUDIO = "AUDIO";
	public static final String VIDEO = "AUDIO";
	public static final String TEXT = "TEXT";

	
	

	public static List<String> getAllContactPhones(
			ContentResolver contentResolver) {
		List<String> lstPhones = new ArrayList<String>();
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
				null);
		Log.i(TAG, "Total no of contacts " + cursor.getCount());

		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(HAS_PHONE_NUMBER)));

				if (hasPhoneNumber > 0) {
					Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
					String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
					String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

					String contact_id = cursor.getString(cursor
							.getColumnIndex(_ID));
					String name = cursor.getString(cursor
							.getColumnIndex(DISPLAY_NAME));

					Cursor phoneCursor = contentResolver.query(
							PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?",
							new String[] { contact_id }, null);
					while (phoneCursor.moveToNext()) {
						Log.d("CONTACT LIST", contact_id);
						lstPhones.add(phoneCursor.getString(
								phoneCursor.getColumnIndex(NUMBER)).replaceAll(
								"[^0-9+]", ""));
					}

					phoneCursor.close();
				}

			}
		}
		return lstPhones;
	}
	
	
	public static void setFriendsContactKeys(
			Context context, List<Friends> lstFNos) {
		ContentResolver contentResolver= context.getContentResolver();
		HashMap<String, String> hm = new HashMap<String, String>();

		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String LOOKUP_KEY = ContactsContract.Contacts.LOOKUP_KEY;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

		Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, null, null,
				null);
		Log.i(TAG, "Total no of contacts in F " + cursor.getCount());
		Log.i(TAG, "Total no of friends in F " + lstFNos);

		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(HAS_PHONE_NUMBER)));

				String lookupKeyMain = cursor.getString(cursor
						.getColumnIndex(LOOKUP_KEY));
				if (hasPhoneNumber > 0) {
					Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
					String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
					String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

					String contact_id = cursor.getString(cursor
							.getColumnIndex(_ID));
					Cursor phoneCursor = contentResolver.query(
							PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?",
							new String[] { contact_id }, null);
					
					while (phoneCursor.moveToNext()) {

						String phoneNumber = phoneCursor.getString(
								phoneCursor.getColumnIndex(NUMBER)).replaceAll(
								"[^0-9+]", "");
						String lookupKey = phoneCursor.getString(phoneCursor
								.getColumnIndex(LOOKUP_KEY));
						Log.d("TEMP", "lookupKeyMain id "+lookupKeyMain +" : phone lookupKey "+lookupKey);
						Log.d(TAG,"Checking phoe no "+phoneNumber);
						for(Friends f : lstFNos){
							Log.d(TAG,"Checking phoe no "+phoneNumber);
							if(f.getIdMobile().equalsIgnoreCase(phoneNumber) || f.getIdMobile().replaceAll("[^0-9+]", "").equalsIgnoreCase(phoneNumber)){
								
								hm.put(lookupKey,phoneNumber );
							}
						}
						

					}

					phoneCursor.close();
				}

			}
		}
		cursor.close();
		Utils.debug(TAG, "final HM : "+hm);
		StateHelper.setHashMap(context, StateHelper.CONTACT_LOOKUP_KEY, hm);
		
	}
	

	 public static Cursor getFriendsCursor(Context context){
		  
				User user = StateHelper.getUser(context);
				List<Friends> lstFriends = StateHelper.getFriends(context);
				
				HashMap<String, String> hm = StateHelper.getHashMap(context, StateHelper.CONTACT_LOOKUP_KEY);
				
				Log.d(TAG," get lookup jeys"+hm.toString());

				String[] args = hm.keySet().toArray(new String[0]);
				Log.d(TAG, " keys "+Arrays.toString(args));
				int argcount = args.length;
				StringBuilder inList = new StringBuilder(argcount * 2);
				for (int i = 0; i < argcount; i++) {
					if (i > 0)
						inList.append(",");
					inList.append("?");
				}

				String select = Contacts.LOOKUP_KEY + " in ( " + inList.toString() + ")";
				Log.d(TAG," get lookup jeys select"+select);
				String[] selectArgs = args;
				String[] contactsProjection = new String[] { Contacts._ID,
						Contacts.DISPLAY_NAME, Contacts.LOOKUP_KEY, Contacts.PHOTO_ID };

				return context.getContentResolver().query(Contacts.CONTENT_URI,
						contactsProjection, select, selectArgs, null);
			
	  }
	
	public static Menu NewMenuInstance(Context context) {
		try {
			Class<?> menuBuilderClass = Class
					.forName("com.android.internal.view.menu.MenuBuilder");

			Constructor<?> constructor = menuBuilderClass
					.getDeclaredConstructor(Context.class);

			return (Menu) constructor.newInstance(context);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	 
	 
	 /*
	  * Dates
	  * 
	  */
	 
	 static DateFormat dateFormat = null;
	 static DateFormat timeFormat = null;
	
	//format according to user's locale
	public static String formatDate(Context context, Date date){
		if(dateFormat==null){
		dateFormat = android.text.format.DateFormat.getDateFormat(context);
		
		}
		
		return dateFormat.format(date);
	}
	
	
	
	//format according to user's locale
	public static String formatTime(Context context, Date date){
		if(timeFormat==null){
		timeFormat = android.text.format.DateFormat.getTimeFormat(context);
		}
		
		return timeFormat.format(date);
	}
	
	static SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd MMM hh:mm");
	public static String formatDateTimeForRem(Context context,Date date){
		if(DateUtils.isToday(date.getTime())){
			DateUtils.getRelativeTimeSpanString(date.getTime());
		}
		return sdfDate.format(date);
	}

	
	static SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM");
	public static String formatDateOnlyForRem(Context context,Date date){
		if(DateUtils.isToday(date.getTime())){
			DateUtils.getRelativeTimeSpanString(date.getTime());
		}
		return sdfDate.format(date);
	}
	
	static SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm");
	public static String formatTimeOnlyForRem(Context context, Date date){
		if(DateUtils.isToday(date.getTime())){
			DateUtils.getRelativeTimeSpanString(date.getTime());
		}
		return sdfTime.format(date);
	}
	
	
	public static int getBackgroudResource(Long inputTime){
		Long now = System.currentTimeMillis();
		Calendar calCurrent = Calendar.getInstance();
		calCurrent.setTimeInMillis(now);
		Calendar calInput = Calendar.getInstance();
		calInput.setTimeInMillis(inputTime);
		
		if(DateUtils.isToday(inputTime)){
			//today and is the reminder pending?
			if(now<inputTime){
				//still pending 
				 return R.drawable.rem_rounded_corner2;
			}
			else{
				//remider time up
				return R.drawable.rem_rounded_corners1;
				
			}
		}
		
		else if(now<inputTime){
		//future	
			return R.drawable.rem_rounded_corner2;
		}
		else if(now>inputTime){
			//past
			return R.drawable.rem_rounded_corners;
		}
		else{
			return R.drawable.rem_rounded_corners;
		}
		
		
		
	}
	
	public static Bitmap mark(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(28);
        paint.setAntiAlias(true);
        paint.setUnderlineText(true);
        canvas.drawText(watermark, 20, 25, paint);

        return result;
    }
	
	private void playDefaultNotificationSound(Context context) {
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(context, notification);
		r.play();
	}
	
	
	private void setRingtone(Context context, File newRingtoneFile){
		ContentValues values = new ContentValues();
		   values.put(MediaStore.MediaColumns.DATA, newRingtoneFile.getAbsolutePath());
		   values.put(MediaStore.MediaColumns.TITLE, "ring");
		   values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
		   values.put(MediaStore.MediaColumns.SIZE, newRingtoneFile.length());
		   values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
		   values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
		   values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
		   values.put(MediaStore.Audio.Media.IS_ALARM, true);
		   values.put(MediaStore.Audio.Media.IS_MUSIC, false);

		   Uri uri = MediaStore.Audio.Media.getContentUriForPath(newRingtoneFile.getAbsolutePath());
		   Uri newUri = context.getContentResolver().insert(uri, values);

		   try {
		       RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
		   } catch (Throwable t) {

		   }
	}
}
