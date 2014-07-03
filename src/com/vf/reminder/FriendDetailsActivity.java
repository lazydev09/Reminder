package com.vf.reminder;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vf.reminder.db.tables.Friends;
import com.vf.reminder.db.tables.User;
import com.vf.reminder.fragments.FriendDetailsFragment;
import com.vf.reminder.fragments.NewReminderFragment;
import com.vf.reminder.utils.QuickContactPhotoHelper;
import com.vf.reminder.utils.StateHelper;
import com.vf.reminder.utils.Utils;

public class FriendDetailsActivity extends FragmentActivity {
	private static final String TAG = "FriendDetailsActivity";
	private  String phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_details);
		ToggleButton tgBlkBtn = (ToggleButton) findViewById(R.id.tgBlkBtn);
		phone = getIntent().getExtras().getString("PHONENO");
		String name =  getIntent().getExtras().getString("NAME");
		Log.d(TAG, "selected details " + phone + " : "+name);
		ImageView photoView = (ImageView)findViewById(R.id.imgPhoto);
		TextView tvName = (TextView)findViewById(R.id.tvName);
		tvName.setText(name);

		QuickContactPhotoHelper photoHelper = new QuickContactPhotoHelper(getApplicationContext());
		photoHelper.addThumbnail(photoView, phone);
		
		
		FriendDetailsFragment reminderFragment = new FriendDetailsFragment();
		Bundle bundle = new Bundle();
		bundle.putString("mobile", phone);
		  FragmentManager fragmentManager = getSupportFragmentManager();
		    fragmentManager.beginTransaction()
		                   .replace(R.id.frd_details_frame, reminderFragment,"FriendDetailsFragment")
		                   .commit();
		    
		   
		   
		   User u = StateHelper.getUser(getApplicationContext());
		   List<Friends> lstFriends = u.getLstFriends();
		   for(Friends f : lstFriends){
       		if(f.getIdMobile().equalsIgnoreCase(phone)){
       			if("ACTIVE".equalsIgnoreCase(f.getStat())){
       				tgBlkBtn.setChecked(true);
       			Utils.debug(TAG, "blocking mobile "+phone);
       			}
       			else{
       				tgBlkBtn.setChecked(false);
       				
       			}
       		}
       		
       	}
		   
		   
		    
			    
	//block button
			    
			    
			    tgBlkBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			        	User u = StateHelper.getUser(getApplicationContext());
			        	List<Friends> lstFriends = u.getLstFriends();
			        	for(Friends f : lstFriends){
			        		if(f.getIdMobile().equalsIgnoreCase(phone)){
			        			if(isChecked){
			        			f.setStat("BLOCKED");
			        			Utils.debug(TAG, "blocking mobile "+phone);
			        			}
			        			else{
			        				f.setStat("ACTIVE");
				        			Utils.debug(TAG, "UN blocking mobile "+phone);
			        			}
			        		}
			        		
			        	}
			        	u.setLstFriends(lstFriends);
			            
			        }
			    });
			    
			    Button btnNewReminder = (Button)findViewById(R.id.btnNewReminder);
			    btnNewReminder.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						NewReminderFragment reminderFragment = new NewReminderFragment();
						Bundle bundle = new Bundle();
						bundle.putString("mobile", phone);
						  FragmentManager fragmentManager = getSupportFragmentManager();
						    fragmentManager.beginTransaction()
						                   .replace(R.id.frd_details_frame, reminderFragment,"NewReminderFragment")
						                   .commit();
						
					}
				});			    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friend_details, menu);
		return true;
	}
	
	

}
