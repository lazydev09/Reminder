package com.vf.reminder.fragments;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vf.reminder.R;
import com.vf.reminder.db.FriendReminderDBHelper;
import com.vf.reminder.db.tables.Reminder;
import com.vf.reminder.utils.AudioHelper;
import com.vf.reminder.utils.Utils;




public class FriendDetailsFragment extends Fragment {
	private static final String TAG = "FriendDetailsFragment";
	// RemListViewAdapter lstAdapter;
		RemCursorAdapter cursorAdapter;
		// footer
		private LinearLayout footer;
		TextView footerMsg;
		private ProgressBar pgbar;
		ListView listView;
		private String mobile;
		// database
		FriendReminderDBHelper dbHelper;
		// audio
		MediaPlayer mediaPlayer = new MediaPlayer();
		Boolean isPlaying = false;
		AudioHelper audHelper = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friend_details, container,
				false);
		listView = (ListView) view.findViewById(R.id.lvFrdDetails);
		footer = (LinearLayout) inflater.inflate(R.layout.list_footer, null);
		 footerMsg = (TextView)footer.findViewById(R.id.tvFooterMsg);
		pgbar = (ProgressBar) footer.findViewById(R.id.pgFooter);
		pgbar.setVisibility(View.VISIBLE);
		dbHelper = new FriendReminderDBHelper(getActivity());
		
		listView.addFooterView(footer);
		
		String mobile = "";
		Bundle bundle = getArguments();
		if(bundle !=null){
			mobile = bundle.getString("mobile");
		}
		
		cursorAdapter = new RemCursorAdapter(getActivity(),
				dbHelper.getAllData(mobile));
		Utils.debug(TAG, "No of rows retrieved 0ncreate "+cursorAdapter.getCount());
		if(cursorAdapter.getCount()==0){
			//show empty message
			footerMsg.setText("Drag and Drop friends to see previous messages");
			footerMsg.setVisibility(View.VISIBLE);
			pgbar.setVisibility(View.GONE);
		}
		listView.setAdapter(cursorAdapter);
		return view;
	}

	

	
	public void setMobileNo(String mobile){
		this.mobile = mobile;
		pgbar.setVisibility(View.VISIBLE);
		cursorAdapter.changeCursor(dbHelper.getAllData(mobile));
		cursorAdapter.notifyDataSetChanged();
		Utils.debug(TAG, "No of rows retrieved ... "+cursorAdapter.getCount());
		if(cursorAdapter.getCount()==0){
			//show empty message
			footerMsg.setText("No Previous reminder sent to Mobile No - +"+mobile);
			footerMsg.setVisibility(View.VISIBLE);
			pgbar.setVisibility(View.GONE);
		}
		
	}
	
	public class RemCursorAdapter extends CursorAdapter {
		public RemCursorAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final View viewFinal = view;
			ViewHolder vh;
			vh = (ViewHolder) view.getTag();

			TextView tvMsg = (TextView) view.findViewById(R.id.tvMsg);
			TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
			int id = cursor.getInt(0);
		//	Utils.debug(TAG, "in bind view column _id" + id);
			final Reminder rem = getCursorToReminder(cursor);
			//Utils.debug(TAG, "in bind view column " + rem.toString());
			view.setBackgroundResource(R.drawable.rem_rounded_corners);
			// audio play
			if ("AUDIO".equalsIgnoreCase(rem.getType())) {
				//
				vh.playButton.setVisibility(View.VISIBLE);
				tvMsg.setText("AUDIO REMINDER - click on audio to hear");
				vh.playButton.setTag("Play");
				vh.playButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d("TAG", "In Play Video click");
						if (!isPlaying) {
							isPlaying = true;
						} else {
							isPlaying = false;
						}
						audHelper.onPlayOnly(isPlaying, rem.getFileLoc());

					}
				});
			}
		

			tvMsg.setText(rem.getMessage());

			Date d = new Date(rem.getDate());
			Log.d(TAG, "Date : in bind view " + d + " : " +rem.getDate());
			// date
			tvDate.setText(Utils.formatDateTimeForRem(getActivity(), d));
			

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view;

			LayoutInflater inflater = (LayoutInflater) FriendDetailsFragment.this
					.getActivity().getSystemService(
							Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.friend_detail_list_item, parent, false);
			setViewHolder(view);

			return view;
		}

	}
	private static class ViewHolder {
		public boolean needInflate;
		ImageView playButton;
		

	}
	private void setViewHolder(View view) {
		ViewHolder vh = new ViewHolder();
		vh.needInflate = false;
		vh.playButton = (ImageView) view.findViewById(R.id.imgPlayVideo);
		view.setTag(vh);
	}

	private Reminder getCursorToReminder(Cursor cursor) {

		Reminder rem = new Reminder();
		rem.setId(cursor.getInt(0));// _id
		rem.setType(cursor.getString(1));// type
		rem.setMessage(cursor.getString(2));// msg
		rem.setDate(cursor.getLong(3));// time
		rem.setFileLoc(cursor.getString(4));// fileloc
		rem.setStatus(cursor.getString(5));// stat
		rem.setFromUser(cursor.getString(6));// fromuser
		// rem.set(cursor.getString(6));//remote id
		return rem;
	}
	
}
