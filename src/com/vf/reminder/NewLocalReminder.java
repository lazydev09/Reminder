package com.vf.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.vf.reminder.fragments.NewReminderFragment;

public class NewLocalReminder extends FragmentActivity {

	private static final String TAG = "NewLocalReminder";
	NewReminderFragment reminderFragment ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_local_reminder);
		    reminderFragment = new NewReminderFragment();
		      FragmentManager fragmentManager = getSupportFragmentManager();
		      
		        fragmentManager.beginTransaction()
			                   .replace(R.id.content_frame, reminderFragment,"NewReminderFragment").addToBackStack(null)
			                   .commit();
		        

			    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_local_reminder, menu);
		return true;
	}
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
		 Log.d(TAG, "in onOptionsItemSelected option"+item.getItemId());
		 switch (item.getItemId()) {
		  case R.id.action_cancel:
			  Intent intent = new Intent();
				this.setResult(FragmentActivity.RESULT_CANCELED, intent);
				this.finish();
	        
	 		    
	            return true;
	            default : return false;
		 }
		 

	 }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		Log.d(TAG, "inside onKeyDown... ."+ keyCode);
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        Log.d(TAG, "inside back....");
	        reminderFragment.handleGoBack();
	       // this.finishActivity(FragmentActivity.RESULT_CANCELED);
	     
	       
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
}
