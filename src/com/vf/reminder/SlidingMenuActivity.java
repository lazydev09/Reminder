package com.vf.reminder;

import java.util.HashMap;

import android.content.ClipData;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.vf.reminder.adapter.ContactSimpleCursorAdapter;
import com.vf.reminder.fragments.NewReminderFragment;
import com.vf.reminder.utils.StateHelper;
import com.vf.reminder.utils.Utils;

public class SlidingMenuActivity extends FragmentActivity {
	
	private static final String TAG = "SlidingMenuActivity";
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
    SimpleCursorAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    String mTitle ="Welcome";
    ImageView imageP;
    String LOOKUP_KEY = ContactsContract.Contacts.LOOKUP_KEY;
    NewReminderFragment reminderFragment;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sliding_menu);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        int width = getResources().getDisplayMetrics().widthPixels/2;
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mDrawerList.getLayoutParams();
        params.width = width;
        mDrawerList.setLayoutParams(params);
         
        mAdapter = new ContactSimpleCursorAdapter(this,
				R.layout.adapter_simple_cursor_contacts, Utils.getFriendsCursor(getApplicationContext()),
				new String[] { Contacts.DISPLAY_NAME, Contacts.LOOKUP_KEY },
				new int[] { R.id.name_entry }, 0);
        mDrawerList.setAdapter(mAdapter);
        //converts cursor to string
	      mAdapter.setCursorToStringConverter(new CursorToStringConverter() {
	        public CharSequence convertToString(Cursor cur) {
	          int index = cur.getColumnIndex(Contacts.DISPLAY_NAME);
	          return cur.getString(index);
	       }});
	      
	       reminderFragment = new NewReminderFragment();
	      FragmentManager fragmentManager = getSupportFragmentManager();
		    fragmentManager.beginTransaction()
		                   .replace(R.id.content_frame, reminderFragment,"NewReminderFragment")
		                   .commit();
		    
		    
		    // Set the list's click listener
		      imageP = (ImageView)findViewById(R.id.imgPlayVideo);
		      mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		        mDrawerList.setOnItemLongClickListener(new DrawerItemLongClickListener());
		       // mDrawerList.setOnDragListener(new DrawerItemOnDragEvent());
		        //mDrawerList.setOnTouchListener(new DrawerItemOnTouchEvent());
		        
		        mDrawerToggle = new ActionBarDrawerToggle(
		                this,                  /* host Activity */
		                mDrawerLayout,         /* DrawerLayout object */
		                R.drawable.ic_launcher,  /* nav drawer icon to replace 'Up' caret */
		                R.string.drawer_open,  /* "open drawer" description */
		                R.string.drawer_close  /* "close drawer" description */
		                ) {

		            /** Called when a drawer has settled in a completely closed state. */
		            public void onDrawerClosed(View view) {
		                super.onDrawerClosed(view);
		                getActionBar().setTitle(mTitle);
		            }

		            /** Called when a drawer has settled in a completely open state. */
		            public void onDrawerOpened(View drawerView) {
		                super.onDrawerOpened(drawerView);
		                getActionBar().setTitle(mTitle);
		            }
		        };
		     // Set the drawer toggle as the DrawerListener
		        mDrawerLayout.setDrawerListener(mDrawerToggle);

		        getActionBar().setDisplayHomeAsUpEnabled(true);
		        
		   
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sliding_menu, menu);
		return true;
	}
	
	private void selectItem(int position) {
	   
	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(position, true);
	   // setTitle(mDrawerList.getSe);
	    mDrawerLayout.closeDrawer(mDrawerList);
	    
	    Toast.makeText(getApplicationContext(), "selected item"+position, Toast.LENGTH_LONG);
	}
	
	//drawer events
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	    	Log.i("SLidingMenuActivity","position :"+position +" id : "+id);
	        selectItem(position);
	    }
	}
	
	private class DrawerItemLongClickListener implements ListView.OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View v,
				int position, long arg3) {
			// TODO Auto-generated method stub
			Log.i(TAG, "LONG CLICKED : " +position+ " : "+v);
			ImageView imageView = new ImageView(getApplicationContext());
			LinearLayout ll = (LinearLayout)v;
			int childcount = ll.getChildCount();
			for (int i=0; i < childcount; i++){
			      View vc = ll.getChildAt(i);
			      Log.i(TAG, "LONG CLICKED : "+vc);    
			      if(vc instanceof ImageView){
			    	  imageView =(ImageView) vc;
			    	  
			      }
			      
			}
			Object obj =  arg0.getItemAtPosition(position);
			Log.i(TAG, "LONG CLICKED : " +obj+ " : "+v);
		     
		        
		        final Cursor cursor = mAdapter.getCursor();
		        // Moves to the Cursor row corresponding to the ListView item that was clicked
		        cursor.moveToPosition(position);
		        
		        String lookupKeyMain = cursor.getString(cursor
						.getColumnIndex(LOOKUP_KEY));
		        HashMap hmLookupKeys = StateHelper.getHashMap(getApplicationContext(), StateHelper.CONTACT_LOOKUP_KEY);
		        String phoneNo = (String)hmLookupKeys.get(lookupKeyMain);
		        ClipData data = ClipData.newPlainText("PHONENO", phoneNo);
		        ClipData.Item item = new ClipData.Item("NOPHOTO");
		        data.addItem(item);
		        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imageView);
		        v.startDrag(data, shadowBuilder, imageView, 0);
		        
			return true;
		}
	
			 
	}


	private static final String DEBUG_TAG = "GESUTE EVENTS";
	 @Override
	  public boolean onTouchEvent(MotionEvent event){ 
	          
	      int action = MotionEventCompat.getActionMasked(event);
	          
	      switch(action) {
	          case (MotionEvent.ACTION_DOWN) :
	              Log.d(DEBUG_TAG,"Action was DOWN");
	              return true;
	          case (MotionEvent.ACTION_MOVE) :
	              Log.d(DEBUG_TAG,"Action was MOVE");
	              return true;
	          case (MotionEvent.ACTION_UP) :
	              Log.d(DEBUG_TAG,"Action was UP");
	              return true;
	          case (MotionEvent.ACTION_CANCEL) :
	              Log.d(DEBUG_TAG,"Action was CANCEL");
	              return true;
	          case (MotionEvent.ACTION_OUTSIDE) :
	              Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
	                      "of current screen element");
	              return true;      
	          default : 
	              return super.onTouchEvent(event);
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
