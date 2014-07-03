package com.vf.reminder.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.vf.reminder.db.tables.Reminder;
import com.vf.reminder.utils.Utils;

public class FriendReminderDBHelper extends  ReminderDBHelper implements SqliteDBHelper,BaseColumns{
	
	
	
	        public static final String TABLE_NAME = "REMINDER_FRIEND";
	        public static final String COLUMN_NAME_ENTRY_ID = "_id";
	        public static final String COLUMN_NAME_TYPE = "TYPE";
	        public static final String COLUMN_NAME_MSG = "MSG";
	        public static final String COLUMN_NAME_TIME = "TIME";
	        public static final String COLUMN_NAME_FILE_LOC = "FILE_KEY";
	        public static final String COLUMN_NAME_STATUS= "STAT";
	        public static final String COLUMN_NAME_REMOTE_ID= "R_ID";
	        public static final String COLUMN_NAME_FROM="FROM_USR";
	        public static final String COLUMN_NAME_TO="TO_USR";
	    
	 
	 private static final String SQL_CREATE_ENTRIES ="CREATE TABLE IF NOT EXISTS "+TABLE_NAME +
				" ( "+COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+COLUMN_NAME_TYPE + " TEXT, " 
				+COLUMN_NAME_MSG + " TEXT, " 
				+COLUMN_NAME_TIME + " INTEGER, " 
				+COLUMN_NAME_FILE_LOC + " TEXT, " 
				+COLUMN_NAME_STATUS + " TEXT, "
				+COLUMN_NAME_REMOTE_ID + " TEXT, "
				+COLUMN_NAME_FROM + " TEXT, "
				+COLUMN_NAME_TO + " TEXT) " ;

	public FriendReminderDBHelper(Context context) {
		super(context);
		getWritableDatabase().execSQL(SQL_CREATE_ENTRIES);
	}

	
	
	
	public long insertFriendReminder(Reminder r){
		
		Utils.debug(TABLE_NAME, "Insert new  record");
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_TYPE, r.getType());
		values.put(COLUMN_NAME_MSG, r.getMessage());
		values.put(COLUMN_NAME_TIME, r.getDate());
		values.put(COLUMN_NAME_FILE_LOC, r.getFileLoc());
		values.put(COLUMN_NAME_STATUS, r.getStatus());
		values.put(COLUMN_NAME_REMOTE_ID, r.getRemoteId());
		values.put(COLUMN_NAME_FROM, r.getFromUser());
		values.put(COLUMN_NAME_TO, r.getToUser());
		return getWritableDatabase().insert(TABLE_NAME, null, values);
	}
	
	
	public Cursor getAllData (String mobileId) {
		SQLiteDatabase db = super.getWritableDatabase();
        String buildSQL = "SELECT * FROM " + TABLE_NAME +" where " + COLUMN_NAME_TO + " = '" + mobileId + "' order by time desc";
 
        Log.d("SQL HELPER", "getAllData SQL: " + buildSQL);
 
        return db.rawQuery(buildSQL, null);
    }
	

}
