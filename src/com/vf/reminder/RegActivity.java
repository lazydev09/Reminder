package com.vf.reminder;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.vf.reminder.aws.ddb.DBResponseHelper;
import com.vf.reminder.aws.ddb.UserDdbAsyncTask;
import com.vf.reminder.db.tables.User;
import com.vf.reminder.utils.GCMUtils;
import com.vf.reminder.utils.PartialRegexInputFilter;
import com.vf.reminder.utils.StateHelper;
import com.vf.reminder.utils.UserDBRequestEnum;
import com.vf.reminder.utils.Utils;
/*
 * 03-09 23:52:56.524: I/GCMUtils(14092): registrationId : APA91bFFO6tXn-7n8K27H-d_7KuTFwcIupH5ZPAuVM_spH-JhCgKSYmJpOMIreSv1rW_KHWA4Im8jFLUR5juAZYo05PgICOcAL1NJ_O4kGP7pyvLd8ztzUOKEqL6q7ZgWjU32N6CzMzG9I3fmln47-U2tEkizskfPQ

 */

public class RegActivity extends Activity  implements   TextWatcher{

	private static final String TAG = "RegActivity";
	
	boolean isEmpty=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);
		final List<String> lstPhones = Utils.getAllContactPhones(this.getContentResolver());
		Log.d(TAG, "No of contacts "+lstPhones.size());
		
		JSONObject object = new JSONObject();
		String locale = this.getResources().getConfiguration().locale.getCountry();
		Log.d(TAG, locale);
		initGCMRegistration();
		Log.i(TAG,object.toString());
		final Button btnReg = (Button)findViewById(R.id.buttonReg);
		final EditText mobileET = (EditText)findViewById(R.id.etMobile);
		final TextView ccRT = (TextView)findViewById(R.id.etCC);
		final EditText emailET = (EditText)findViewById(R.id.etEmail);
		
		mobileET.addTextChangedListener(this);
		ccRT.addTextChangedListener(this);
		emailET.addTextChangedListener(this);
		//empty validation
		mobileET.setOnFocusChangeListener(new EmptyOnFocusChangeListner());
		emailET.setOnFocusChangeListener(new EmptyOnFocusChangeListner());
		
		// handle country codes
		ccRT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopupMenu popup = new PopupMenu(getApplicationContext(), v);
				getMenuInflater().inflate(R.menu.country, popup.getMenu());
				popup.show();
				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						ccRT.setText(item.getTitle());
						return false;
					}
				});
				
			}
		});
		
		btnReg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String mobileNo = mobileET.getText().toString();
				String country = ccRT.getText().toString();
				String id = country + mobileNo;
				Log.d("IS EMPTY", isEmpty+"");
				if(isEmpty){
					
					return ;
				}
				
				User newUser = new User();
				newUser.setMobile(id);
				newUser.setCountry(country);
				newUser.setTime(System.currentTimeMillis());
				newUser.setRegId(StateHelper.getRegistrationId(getApplicationContext()));
				//newUser.setRegId("APA91bFFO6tXn-7n8K27H-d_7KuTFwcIupH5ZPAuVM_spH-JhCgKSYmJpOMIreSv1rW_KHWA4Im8jFLUR5juAZYo05PgICOcAL1NJ_O4kGP7pyvLd8ztzUOKEqL6q7ZgWjU32N6CzMzG9I3fmln47-U2tEkizskfPQ");
				//newUser.setRegId("EMULATOR");
				//save and get User obj
				getNewUser(newUser, lstPhones);
				
				
				new AlertDialog.Builder(RegActivity.this).setTitle("Waiting for PIN").setMessage("Verifying the SMS... Please wait .. TODO ignore...").setNeutralButton("Manual Enter", null).
				setCancelable(false).show();
				
				//temp sms code
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {

					
						

					}
				}, 5 * 1000);
				
				
				setContentView(R.layout.activity_reg_res);
				final Button btnVer = (Button)findViewById(R.id.btnVerify);
				final EditText pinET = (EditText)findViewById(R.id.etPin);
				btnVer.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String verPin = pinET.getText().toString();
						Log.i(TAG, "verifiy pin : "+verPin);
						//setContentView(R.layout.activity_main);
						StateHelper.setUserStat(getApplicationContext(), StateHelper.REGISTERED);
						Intent i = new Intent(getApplicationContext(),MainActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(i);
						finish();
						
					}
				});
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reg, menu);
		
		return true;
	}

	
	private void createMenu(Context context){
		Menu menu = Utils.NewMenuInstance(context);
		getMenuInflater().inflate(R.menu.country, menu);
		menu.add("");
	}

	private void getNewUser(User newUser,List<String> contacts){
		new UserDdbAsyncTask(UserDBRequestEnum.CREATE_USER,contacts){

			@Override
			protected void onPostExecute(DBResponseHelper<User> result) {
				// TODO Auto-generated method stub
				Utils.debug(TAG,"in postExecute.."+result.toString());
				Utils.debug(TAG,"in postExecute. errmsg."+result.getErrMessage());
				
				if(result.isSuccess()){
					Intent intent = new Intent(RegActivity.this,
							MainActivity.class);
					startActivity(intent);

					RegActivity.this.finish();
				}
				else{
					Toast.makeText(getApplicationContext(), "Failed to create Account, Please try again later", Toast.LENGTH_LONG).show();
				}
				StateHelper.setUser(getApplicationContext(), result.getT());
			}
		}.execute(newUser);
	  }
	

	 //text watcher methods
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
	//	Log.i("AFTER CHANGED", s.toString());
		s.setFilters(new InputFilter[] {  
		        new PartialRegexInputFilter("([0-9a-zA-z@. ]*)?$")
	    }  );
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
	
	
	public class EmptyOnFocusChangeListner implements OnFocusChangeListener{
		boolean isFocused = false;
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				isFocused = true;
			}
			
			if(isFocused){
				if(!hasFocus){
					EditText et = (EditText)v;
					
					if(et.getText().toString().isEmpty()){
					Toast.makeText(getApplicationContext(),"Value is null",Toast.LENGTH_SHORT).show();
					isEmpty=true;
					}
					else{
						isEmpty=false;
					}
				}
			}
			
		}
		
	}
	
	 private void initGCMRegistration(){
			//CommonUtils.getUserProfile(getContentResolver());
			try {
				Log.d(TAG,"in initGCMRegistration" );
				if(checkPlayServices()){
					GCMUtils gcmUtils = new GCMUtils(getApplicationContext());
					gcmUtils.register();
					
					Toast.makeText(getApplicationContext(), "reg : "+StateHelper.getRegistrationId(getApplicationContext()), Toast.LENGTH_LONG).show();
				}
				    
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
	 
	 private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	  private boolean checkPlayServices() {
	        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
	        if (resultCode != ConnectionResult.SUCCESS) {
	            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
	            } else {
	                Log.i(TAG, "This device is not supported.");
	                finish();
	            }
	            return false;
	        }
	        return true;
	    }
	
	
}
