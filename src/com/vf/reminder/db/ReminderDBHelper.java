package com.vf.reminder.db;


import static com.vf.reminder.db.ReminderDBContract.ReminderTable.COLUMN_NAME_ENTRY_ID;
import static com.vf.reminder.db.ReminderDBContract.ReminderTable.COLUMN_NAME_FILE_LOC;
import static com.vf.reminder.db.ReminderDBContract.ReminderTable.COLUMN_NAME_FROM;
import static com.vf.reminder.db.ReminderDBContract.ReminderTable.COLUMN_NAME_MSG;
import static com.vf.reminder.db.ReminderDBContract.ReminderTable.COLUMN_NAME_REMOTE_ID;
import static com.vf.reminder.db.ReminderDBContract.ReminderTable.COLUMN_NAME_STATUS;
import static com.vf.reminder.db.ReminderDBContract.ReminderTable.COLUMN_NAME_TIME;
import static com.vf.reminder.db.ReminderDBContract.ReminderTable.COLUMN_NAME_TYPE;
import static com.vf.reminder.db.ReminderDBContract.ReminderTable.TABLE_NAME;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.vf.reminder.db.tables.Reminder;
import com.vf.reminder.utils.Utils;


public class ReminderDBHelper extends SQLiteOpenHelper implements SqliteDBHelper {
	
	
	
	private String[] allColumns = { COLUMN_NAME_ENTRY_ID, COLUMN_NAME_TYPE,
			COLUMN_NAME_MSG, COLUMN_NAME_TIME, COLUMN_NAME_FILE_LOC,
			COLUMN_NAME_STATUS, COLUMN_NAME_FROM , COLUMN_NAME_REMOTE_ID};
	
	private static final String SQL_CREATE_ENTRIES ="CREATE TABLE IF NOT EXISTS "+TABLE_NAME +
			" ( "+COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+COLUMN_NAME_TYPE + " TEXT, " 
			+COLUMN_NAME_MSG + " TEXT, " 
			+COLUMN_NAME_TIME + " INTEGER, " 
			+COLUMN_NAME_FILE_LOC + " TEXT, " 
			+COLUMN_NAME_STATUS + " TEXT, "
			+COLUMN_NAME_FROM + " TEXT, "
			+COLUMN_NAME_REMOTE_ID + " TEXT) " ;
	
	
	 private long now;
	 public ReminderDBHelper(Context context){
		 super(context, DATABASE_NAME, null, DATABASE_VERSION);
		  now = System.currentTimeMillis();
	 }
	 

	

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(DATABASE_NAME,"creating new database : " +SQL_CREATE_ENTRIES);
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		  db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		  onCreate(db);
	}
	
	
	public long insertReminder(RemDBObjHelper helper){
		
		Utils.debug(TABLE_NAME, "Insert new  record");
		ContentValues values = new ContentValues();
		//values.put(COLUMN_NAME_ENTRY_ID, helper.getId()); primary key is autoincrement
		values.put(COLUMN_NAME_TYPE, helper.getType());
		values.put(COLUMN_NAME_MSG, helper.getMsg());
		values.put(COLUMN_NAME_TIME, helper.getTime());
		values.put(COLUMN_NAME_FILE_LOC, helper.getFile_loc());
		values.put(COLUMN_NAME_STATUS, helper.getStat());
		values.put(COLUMN_NAME_FROM, helper.getFrom());
		values.put(COLUMN_NAME_REMOTE_ID, helper.getR_id());
		return getWritableDatabase().insert(TABLE_NAME, null, values);
	}
	
	
	public long insertReminder(Reminder helper){
		Log.d(DATABASE_NAME,"insert new table");
		ContentValues values = new ContentValues();
		//values.put(COLUMN_NAME_ENTRY_ID, helper.getId()); primary key is autoincrement
		values.put(COLUMN_NAME_TYPE, helper.getType());
		values.put(COLUMN_NAME_MSG, helper.getMessage());
		values.put(COLUMN_NAME_TIME, Long.valueOf(helper.getDate()));
		values.put(COLUMN_NAME_FILE_LOC, helper.getFileLoc());
		values.put(COLUMN_NAME_STATUS, helper.getStatus());
		if(helper.getFromUser()==null){
			values.put(COLUMN_NAME_FROM, "ME");
		}
		else{
			values.put(COLUMN_NAME_FROM, helper.getFromUser());
		}
		
		values.put(COLUMN_NAME_REMOTE_ID, helper.getId());
		return getWritableDatabase().insert(TABLE_NAME, null, values);
	}
	
	
	public int updateRemRemoteId(String id, String remoteId){
		if(remoteId==null || remoteId.isEmpty()){
			return -2;
		}
		// New value for one column
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_REMOTE_ID, remoteId);
		// Which row to update, based on the ID
		String selection = COLUMN_NAME_REMOTE_ID + " ";
		String[] selectionArgs = { String.valueOf(id) };
		
		return getWritableDatabase().update(
			    TABLE_NAME,
			    values,
			    selection,
			    selectionArgs);
	}
	
	
	
	
	public int updateNewRemTime(long rowId, long time){
		Utils.debug("DB", "updating time ........"+time + ": to row id ... "+rowId);
		// New value for one column
		String where = COLUMN_NAME_ENTRY_ID + " = " +rowId;
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_TIME, time);
		// Which row to update, based on the ID
		
		return getWritableDatabase().update(
			    TABLE_NAME,
			    values,
			    where,
			    null);
	}
	
	
	public int deleteReminder(long rowId){
		Utils.debug("DB", "Deleting row id........"+rowId);
		String where = COLUMN_NAME_ENTRY_ID + " = " +rowId;
		return getWritableDatabase().delete(TABLE_NAME, where, null);
	}
	
	public Reminder getReminder(long _id){
		String sql = "select * from "+TABLE_NAME+" where "+COLUMN_NAME_ENTRY_ID+ " = "+_id;
		Cursor cursor = getWritableDatabase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			return getCursorToReminder(cursor);
		}
		return null;
	}
	
	public List<Reminder> getAllPendingReminders(){
		
		String selectQuery = "SELECT  * FROM "+TABLE_NAME+" where "+COLUMN_NAME_TIME+ " >"+  now +" order by " + COLUMN_NAME_TIME +" asc";
		return getAllReminders(selectQuery);
	}
	
	public List<Reminder> getAllExpiredLocalReminders(){
		String selectQuery = "SELECT  * FROM "+TABLE_NAME+" where "+COLUMN_NAME_TIME+ " < "+  now +" order by " + COLUMN_NAME_TIME +" desc";
		return getAllReminders(selectQuery);
	}
	
	public List<Reminder> getAllReminders(String selectQuery){
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		List<Reminder> lstRem = new ArrayList<Reminder>();
		Log.d("DB","total no of rows "+cursor.getColumnCount());
		cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Reminder rem = new Reminder();
				rem.setId(cursor.getInt(0));
				rem.setType(cursor.getString(1));
				rem.setMessage(cursor.getString(2));
				rem.setDate(cursor.getLong(3));
				rem.setFileLoc(cursor.getString(4));
				rem.setStatus(cursor.getString(5));
				rem.setFromUser(cursor.getString(6));
				lstRem.add(rem);
				
			cursor.moveToNext();
		}
			cursor.close();
			return lstRem;
	}
	
	public void findByRemoteId(String remoteId){
		String selectQuery = "SELECT  * FROM " + TABLE_NAME +"where "+COLUMN_NAME_REMOTE_ID+ "="+remoteId;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		Log.d("DB","total no of rows "+cursor.getColumnCount());
		if (cursor.moveToFirst()) {
			Log.d("DB", cursor.getInt(0)+"");
			Log.d("DB", cursor.getString(1)+"");
		}
	}
	

	
	
	public Cursor getAllData () {
		SQLiteDatabase db = this.getWritableDatabase();
        String buildSQL = "SELECT * FROM " + TABLE_NAME +" where " + COLUMN_NAME_STATUS + " in ('I')";
 
        Log.d("SQL HELPER", "getAllData SQL: " + buildSQL);
 
        return db.rawQuery(buildSQL, null);
    }
	
	public Cursor getAllData(long time) {
		
		Utils.debug("DB", "****** current time ******"+new Date(1394792450818L));
		SQLiteDatabase db = this.getWritableDatabase();
		String sql1 = " select * , time as time1, '' as time2 from REMINDER where time >  1394792450818 " +
				"union select * , '' as time1, time as time2 from REMINDER where time < 1394792450818" +
				" order by time1 asc, time2 desc";
        Cursor c = db.rawQuery(sql1, null);
        c.moveToFirst();
        return c;
    }
	
private Reminder getCursorToReminder(Cursor cursor){
		
		Reminder rem = new Reminder();
		rem.setId(cursor.getInt(0));//_id
		rem.setType(cursor.getString(1));//type
		rem.setMessage(cursor.getString(2));//msg
		rem.setDate(cursor.getLong(3));//time
		rem.setFileLoc(cursor.getString(4));//fileloc
		rem.setStatus(cursor.getString(5));//stat
		rem.setFromUser(cursor.getString(6));//fromuser
		//rem.set(cursor.getString(6));//remote id
		return rem;
	}

}

final class ReminderDBContract {
	ReminderDBContract(){
		
	}
	
	 public static abstract class ReminderTable implements BaseColumns {
	        public static final String TABLE_NAME = "REMINDER";
	        public static final String COLUMN_NAME_ENTRY_ID = "_id";
	        public static final String COLUMN_NAME_TYPE = "TYPE";
	        public static final String COLUMN_NAME_MSG = "MSG";
	        public static final String COLUMN_NAME_TIME = "TIME";
	        public static final String COLUMN_NAME_FILE_LOC = "FILE_LOC";
	        public static final String COLUMN_NAME_STATUS= "STAT";
	        public static final String COLUMN_NAME_REMOTE_ID= "R_ID";
	        public static final String COLUMN_NAME_FROM="FROM_USR";
	    }
	 
	 
}

