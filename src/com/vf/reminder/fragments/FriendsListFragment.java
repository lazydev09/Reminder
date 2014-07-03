package com.vf.reminder.fragments;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.vf.reminder.FriendDetailsActivity;
import com.vf.reminder.R;
import com.vf.reminder.adapter.ContactSimpleCursorAdapter;
import com.vf.reminder.aws.ddb.DBResponseHelper;
import com.vf.reminder.aws.ddb.UserDdbAsyncTask;
import com.vf.reminder.db.tables.User;
import com.vf.reminder.utils.StateHelper;
import com.vf.reminder.utils.UserDBRequestEnum;
import com.vf.reminder.utils.Utils;

public class FriendsListFragment extends Fragment{
	private static final String TAG = "FriendsListFragment";
	SimpleCursorAdapter mAdapter;
	ImageView imageP;
	private ListView fList;
	String LOOKUP_KEY = ContactsContract.Contacts.LOOKUP_KEY;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friends, container,
				false);
		setHasOptionsMenu(true);

		fList = (ListView) view.findViewById(R.id.lstFrds);
		mAdapter = new ContactSimpleCursorAdapter(getActivity(),
				R.layout.adapter_simple_cursor_contacts, Utils.getFriendsCursor(getActivity().getApplicationContext()),
				new String[] { Contacts.DISPLAY_NAME, Contacts.LOOKUP_KEY },
				new int[] { R.id.name_entry }, 0);

		fList.setAdapter(mAdapter);
		
		
		fList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Cursor cursor = mAdapter.getCursor();
				cursor.moveToPosition(position);
				String lookupKeyMain = cursor.getString(cursor
						.getColumnIndex(LOOKUP_KEY));
		        HashMap hmLookupKeys = StateHelper.getHashMap(getActivity().getApplicationContext(), StateHelper.CONTACT_LOOKUP_KEY);
		        String phoneNo = (String)hmLookupKeys.get(lookupKeyMain);
			    Intent intent = new Intent(getActivity().getApplicationContext(),FriendDetailsActivity.class);
			    intent.putExtra("PHONENO", phoneNo);
			    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
			    intent.putExtra("NAME", name);
			    startActivity(intent);
			}
		});
	
		// converts cursor to string
		mAdapter.setCursorToStringConverter(new CursorToStringConverter() {
			public CharSequence convertToString(Cursor cur) {
				int index = cur.getColumnIndex(Contacts.DISPLAY_NAME);
				return cur.getString(index);
			}
		});
		doRefresh();
		
		return view;
	}
	
	
	private void reloadFriendsList(){
		
		mAdapter.changeCursor(Utils.getFriendsCursor(getActivity().getApplicationContext()));
		mAdapter.notifyDataSetChanged();
	}

	private Menu optionsMenu;
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		optionsMenu = menu;
		inflater.inflate(R.menu.refresh_friends, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.action_refresh_friends:
	    	Log.d(TAG, "On Refreshed called");
	    	
	        return false;
	    default:
	       return false;
	    }
	}
	
	public void setRefreshActionButtonState(final boolean refreshing) {
	    if (optionsMenu != null) {
	        final MenuItem refreshItem = optionsMenu
	            .findItem(R.id.action_refresh_friends);
	        if (refreshItem != null) {
	            if (refreshing) {
	                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
	                getActivity().setTitle("Refreshing friends List");
	               
	            } else {
	                refreshItem.setActionView(null);
	                getActivity().setTitle("Friends Synchronized");
	            }
	        }
	    }
	}
	
	public void doRefresh(){
		setRefreshActionButtonState(true);
		List<String> allContacts = Utils.getAllContactPhones(getActivity().getContentResolver());
		User u = StateHelper.getUser(getActivity());
		Utils.debug(TAG, "SENDING USER : "+u.toString());
		new UserDdbAsyncTask(UserDBRequestEnum.REFRESH_FRIENDS,allContacts){

			@Override
			protected void onPostExecute(DBResponseHelper<User> result) {
				// TODO Auto-generated method stub
				setRefreshActionButtonState(false);
				Utils.debug(TAG,"in postExecute.."+result.toString());
				Utils.debug(TAG,"in postExecute. errmsg."+result.getErrMessage());
				if(result.isSuccess()){
					User newUser = result.getT();
					Utils.debug(TAG, "Saving new User"+newUser.toString());
					StateHelper.setUser(getActivity(), newUser);
					reloadFriendsList();
				}
				
			}
		}.execute(u);
	}

}
