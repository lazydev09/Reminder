package com.vf.reminder.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vf.reminder.db.tables.Reminder;
import static com.vf.reminder.db.ReminderDBContract.ReminderTable.*;

public class RemDataSource {
	// Database fields
	private SQLiteDatabase database;
	private ReminderDBHelper dbHelper;
	private String[] allColumns = { COLUMN_NAME_ENTRY_ID, COLUMN_NAME_FROM,
			COLUMN_NAME_MSG, COLUMN_NAME_FILE_LOC, COLUMN_NAME_STATUS,
			COLUMN_NAME_TIME, COLUMN_NAME_TYPE };

	public RemDataSource(Context context) {
		dbHelper = new ReminderDBHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public List<Reminder> getAllReminder() {
		List<Reminder> lstReminders = new ArrayList<Reminder>();
		Cursor cursor = database.query(TABLE_NAME, allColumns, null, null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Reminder rem = new Reminder();
			rem.setId(cursor.getInt(0));
			rem.setType(cursor.getString(1));
			rem.setMessage(cursor.getString(2));
			rem.setDate(cursor.getLong(3));
			rem.setFileLoc(cursor.getString(4));
			rem.setStatus(cursor.getString(5));
			rem.setFromUser(cursor.getString(7));
			lstReminders.add(rem);

			cursor.moveToNext();
		}
		cursor.close();
		return lstReminders;
	}

}
