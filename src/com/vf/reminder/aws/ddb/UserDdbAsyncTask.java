package com.vf.reminder.aws.ddb;

import java.util.List;

import android.os.AsyncTask;

import com.vf.reminder.db.tables.Friends;
import com.vf.reminder.db.tables.User;
import com.vf.reminder.utils.StateHelper;
import com.vf.reminder.utils.UserDBRequestEnum;

public class UserDdbAsyncTask extends AsyncTask<User, Void, DBResponseHelper<User>> {
	
	UserDBRequestEnum request;
	List<String> contacts;
	
	public UserDdbAsyncTask(UserDBRequestEnum request) {
		this.request = request;
	}
	
	public UserDdbAsyncTask(UserDBRequestEnum request, List<String> contacts) {
		this.request = request;
		this.contacts=contacts;
	}
	
	private static final String TAG = "DdbAsyncTask";
	
	
	
	@Override
	protected DBResponseHelper<User> doInBackground(User... params) {
		
		switch(request){
		case CREATE_USER : {
			DdbManager db = new DdbManager();
			return db.newUser(params[0],contacts);
		}
		case REFRESH_FRIENDS : {
			User user = params[0];
			DdbManager db = new DdbManager();
			List<Friends> lstFriends = db.refreshFriends(contacts, user);
			DBResponseHelper<User> result = new DBResponseHelper<User>();
			result.setSuccess(true);
			user.setLstFriends(lstFriends);
			result.setT(user);
			return result;
		}
		default : return null;
		}
		
	}

	
	
}


