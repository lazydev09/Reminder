package com.vf.reminder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.amazonaws.tvmclient.Response;
import com.vf.reminder.aws.tvm.AmazonClientManager;
import com.vf.reminder.db.tables.Friends;
import com.vf.reminder.db.tables.User;
import com.vf.reminder.fragments.FriendsListFragment;
import com.vf.reminder.fragments.ReminderLstFragment;
import com.vf.reminder.utils.StateHelper;
import com.vf.reminder.utils.Utils;

public class MainActivity extends FragmentActivity {

	private static final String TAG = "MainActivity";

	// aws tvm
	public static AmazonClientManager clientManager = null;
	private static final String fail = "Load Failed. Please Try Restarting the Application.";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		clientManager = new AmazonClientManager(
				StateHelper.getSharedPreferences(getApplicationContext()));

		if (MainActivity.clientManager.hasCredentials()) {
			Utils.debug(TAG, "WITH Credentails");

		} else {
			new ValidateCredentialsTask().execute();
		}

		// check user status
		User user = StateHelper.getUser(getApplicationContext());
		Log.d(TAG, "USer status " + user);
		List<String> lst = new ArrayList<String>();
		lst.add("2");

		if (user == null) {
			// new user
			// Intent intent = new Intent(this,RegActivity.class);
			Intent intent = new Intent(this, RegActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		

		// refresh LookupKeys
		Utils.setFriendsContactKeys(getApplicationContext(),
				user.getLstFriends());
		

		ReminderLstFragment fragment = new ReminderLstFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.main_frame, fragment, "ReminderLstFragment")
				.commit();

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "in onOptionsItemSelected option" + item.getItemId());
		switch (item.getItemId()) {
		case R.id.action_friends:
			// search action
			Log.d(TAG, "in friends option");
			FriendsListFragment friendsListFragment = new FriendsListFragment();

			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager
					.beginTransaction().addToBackStack(null)
					.replace(R.id.main_frame, friendsListFragment,
							"FriendDetailsFragment").commit();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class ValidateCredentialsTask extends
			AsyncTask<Void, Void, Response> {

		protected Response doInBackground(Void... params) {
			return MainActivity.clientManager.validateCredentials();
		}

		protected void onPostExecute(Response response) {
			if (response != null && response.requestWasSuccessful()) {
				Utils.debug(TAG, "Validation sucessful");
			} else {
				Utils.debug(TAG, "validate credentials failed");
			}
		}

	}
}
