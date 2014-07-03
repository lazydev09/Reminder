package com.vf.reminder.fragments;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vf.reminder.NewLocalReminder;
import com.vf.reminder.R;
import com.vf.reminder.SlidingMenuActivity;
import com.vf.reminder.alarm.AlarmHelper;
import com.vf.reminder.db.ReminderDBHelper;
import com.vf.reminder.db.tables.Reminder;
import com.vf.reminder.utils.AudioHelper;
import com.vf.reminder.utils.DateTimeMenuEnum;
import com.vf.reminder.utils.ExpandAnimation;
import com.vf.reminder.utils.MyDialogResult;
import com.vf.reminder.utils.QuickContactPhotoHelper;
import com.vf.reminder.utils.Utils;

public class ReminderLstFragment extends Fragment {
	private static final String TAG = "ReminderLstFragment";
	// RemListViewAdapter lstAdapter;
	RemCursorAdapter cursorAdapter;
	QuickContactPhotoHelper qhelper;

	// footer
	private LinearLayout footer;
	private LinearLayout layoutHeader;
	private ProgressBar pgbar;
	private TextView tvFooterMsg;
	ListView listView;

	// database
	ReminderDBHelper dbHelper;
	// List<Reminder> lstLocalR;

	// extra buttons
	LinearLayout layoutFullMsg;
	static final int SELF_REMINDER = 10;
	static final int FRIEND_REMINDER = 20;

	// frame
	FrameLayout frameLayout;

	// audio
	MediaPlayer mediaPlayer = new MediaPlayer();
	Boolean isPlaying = false;
	AudioHelper audHelper = null;

	// alarm
	AlarmHelper alarm = new AlarmHelper();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_reminders, container,
				false);
		listView = (ListView) view.findViewById(R.id.remLstView);
		layoutHeader = (LinearLayout) inflater.inflate(
				R.layout.rem_list_header, null);
		footer = (LinearLayout) inflater.inflate(R.layout.list_footer, null);
		pgbar = (ProgressBar) footer.findViewById(R.id.pgFooter);
		pgbar.setVisibility(View.GONE);
		tvFooterMsg = (TextView) footer.findViewById(R.id.tvFooterMsg);
		qhelper = new QuickContactPhotoHelper(getActivity());
		listView.addFooterView(footer);
		listView.addHeaderView(layoutHeader);
		audHelper = new AudioHelper(getActivity(),true);


		// framelay
		frameLayout = (FrameLayout) inflater.inflate(
				R.layout.frame_layout_text_view, null);

		// lstAdapter = new RemListViewAdapter();
		dbHelper = new ReminderDBHelper(getActivity());
		cursorAdapter = new RemCursorAdapter(getActivity(),
				dbHelper.getAllData(System.currentTimeMillis()));
		
		if(cursorAdapter.getCount()==0){
			tvFooterMsg.setVisibility(View.VISIBLE);
			tvFooterMsg.setText("No Reminder, You sent Reminders from the above button");
		}
		// listView.setAdapter(lstAdapter);
		listView.setAdapter(cursorAdapter);
		listView.setFocusable(true);

		// item click
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg1.getTag() == null) {
					// clicked on header, ignore
					return;
				}
				Log.d(TAG, "in onItemClick audio list" + arg1.getTag()
						+ " arg2 : " + arg2 + " arg3 : " + arg3);
				// final Reminder r = (Reminder) lstAdapter.getItem(arg2);
				final Reminder r = getCursorToReminder(cursorAdapter
						.getCursor());

				ImageView imgAud = (ImageView) arg1.findViewById(R.id.imgAud);

				TextView tvMsg = (TextView) arg1.findViewById(R.id.tvMsg);
				if (View.GONE == tvMsg.getVisibility()) {
					tvMsg.setVisibility(View.VISIBLE);
				} else {
					tvMsg.setVisibility(View.GONE);
				}
				layoutFullMsg = (LinearLayout) arg1
						.findViewById(R.id.layoutFull);

				ExpandAnimation expandAni = new ExpandAnimation(layoutFullMsg,
						0);
				layoutFullMsg.startAnimation(expandAni);

				TextView tvFull = (TextView) layoutFullMsg
						.findViewById(R.id.tvFull);
				tvFull.setMaxLines(Integer.MAX_VALUE);
				tvFull.setText(tvMsg.getText());

			}
		});

		// self reminder
		Button btnMe = (Button) view.findViewById(R.id.btnMe);
		btnMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						NewLocalReminder.class);
				startActivityForResult(intent, SELF_REMINDER);

			}
		});

		Button btnFrd = (Button) view.findViewById(R.id.btnFrd);
		btnFrd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						SlidingMenuActivity.class);
				startActivityForResult(intent, FRIEND_REMINDER);

			}
		});

		return view;
	}

	private static class ViewHolder {
		public boolean needInflate;
		ImageView imgDelBtn;
		ImageView playButton;
		ImageView imgForBtn;
		TextView tvDateBtn;
		TextView tvTimeBtn;

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
			TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
			TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
			ImageView photo = (ImageView) view.findViewById(R.id.ivRemPhoto);
			int id = cursor.getInt(0);
		//	Utils.debug(TAG, "in bind view column _id" + id);
			final Reminder rem = getCursorToReminder(cursor);
			view.setBackgroundResource(Utils.getBackgroudResource(rem.getDate()));
			// delete
			vh.imgDelBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					alarm.CancelAlarm(getActivity(), rem);
					deleteCell(viewFinal, rem.getId());
					if (rem.getType() == Utils.AUDIO) {
						// delete file
						try {
							File f = new File(rem.getFileLoc());
							f.delete();
						} catch (Exception ex) {
							Utils.debug(TAG,
									"failed to delete file" + ex.getMessage());
						}

					}

				}
			});
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
						Utils.toast(getActivity(), "inside click event"+isPlaying);
						if (!isPlaying) {
							isPlaying = true;
						} else {
							isPlaying = false;
						}
						audHelper.onPlayOnly(isPlaying, rem.getFileLoc());

					}
				});
			}
			// forward
			vh.imgForBtn.setOnClickListener(new ImageOnClickListner(viewFinal,
					rem.getId(), rem));

			if (rem.getFromUser() != null) {
				Log.d(TAG, "sending phone no " + rem.getFromUser());
				qhelper.addThumbnail(photo, rem.getFromUser());

			}

			tvMsg.setText(rem.getMessage());

			Date d = new Date(rem.getDate());
			Log.d(TAG, "Date : in bind view " + d + " : " +rem.getDate());
			// time
			tvTime.setText(Utils.formatTimeOnlyForRem(getActivity(), d));
			vh.tvTimeBtn.setOnClickListener(new TimeOnClickListner(rem));
			// date
			tvDate.setText(Utils.formatDateOnlyForRem(getActivity(), d));
			vh.tvDateBtn.setOnClickListener(new DateOnClickListner(rem));

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view;

			LayoutInflater inflater = (LayoutInflater) ReminderLstFragment.this
					.getActivity().getSystemService(
							Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.rem_list_item, parent, false);
			setViewHolder(view);

			return view;
		}

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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "inside onActivityResult");
		if (requestCode == SELF_REMINDER) {
			if (resultCode == FragmentActivity.RESULT_OK) {

				Log.d(TAG, "****** back to fragment*****");

				if (data.getExtras().getBoolean("ONBACK")) {
					Log.d(TAG, "******ON back in main fragment*****");
					return;
				}

				layoutHeader.findViewById(R.id.NoRemMsgHeader).setVisibility(
						View.GONE);
				// lstAdapter.notifyDataSetChanged();

				cursorAdapter.changeCursor(dbHelper.getAllData(System
						.currentTimeMillis()));
				cursorAdapter.notifyDataSetChanged();

			}
		}
	}

	class ImageOnClickListner implements OnClickListener {
		final View view;
		long position;
		Reminder r;

		ImageOnClickListner(final View view, long position, Reminder r) {
			this.view = view;
			this.position = position;
			this.r = r;
		}

		@Override
		public void onClick(View v) {
			if (R.id.imgDel == v.getId()) {
				Log.d(TAG, "inside delete");

			}
			if (R.id.imgFor == v.getId()) {
				Log.d(TAG, "inside forward");
				ViewHolder vh = (ViewHolder) view.getTag();
				Log.d(TAG, "Position selected" + position);

				Intent intent = new Intent(getActivity(),
						SlidingMenuActivity.class);
				intent.putExtra(Utils.TYPE, Utils.FORWARD);
				intent.putExtra(Utils.FORWARD, r);
				startActivityForResult(intent, FRIEND_REMINDER);
				getActivity().overridePendingTransition(R.anim.activity_slide1,
						R.anim.activity_slide2);
			}

		}

	}

	private void setViewHolder(View view) {
		ViewHolder vh = new ViewHolder();
		vh.needInflate = false;
		vh.imgDelBtn = (ImageView) view.findViewById(R.id.imgDel);
		vh.playButton = (ImageView) view.findViewById(R.id.imgPlayVideo);
		vh.imgForBtn = (ImageView) view.findViewById(R.id.imgFor);
		vh.tvDateBtn = (TextView) view.findViewById(R.id.tvDate);
		vh.tvTimeBtn = (TextView) view.findViewById(R.id.tvTime);
		view.setTag(vh);
	}

	private void deleteCell(final View v, final long index) {
		AnimationListener al = new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {

				ViewHolder vh = (ViewHolder) v.getTag();
				vh.needInflate = true;
				dbHelper.deleteReminder(index);
				cursorAdapter.notifyDataSetChanged();

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		};

		collapse(v, al);
	}

	private void collapse(final View v, AnimationListener al) {
		final int initialHeight = v.getMeasuredHeight();

		Animation anim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight
							- (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		if (al != null) {
			anim.setAnimationListener(al);
		}
		anim.setDuration(1000);
		v.startAnimation(anim);
	}

	class DateOnClickListner implements OnClickListener, MyDialogResult,
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5463413318712558184L;
		Reminder r;

		DateOnClickListner(Reminder r) {
			this.r = r;
		}

		@Override
		public void onClick(View v) {
			PopupMenu popup = new PopupMenu(getActivity(), v);
			final TextView dateTime = (TextView) v;
			final DateOnClickListner thisObj = this;

			getActivity().getMenuInflater().inflate(R.menu.date,
					popup.getMenu());
			final TextView dateView = (TextView) v;
			popup.show();

			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Log.d("DEDE",
							item.getTitle() + " : " + R.string.item_date_select
									+ " item id " + item.getItemId());

					if (item.getTitle()
							.equals(getResources().getString(
									R.string.item_date_select))) {

						DialogFragment newFragment = new DatePickerFragment();
						Bundle bundle = new Bundle();
						bundle.putString("ReqestFragmentTag",
								ReminderLstFragment.class.getSimpleName());
						bundle.putSerializable("objListner", thisObj);
						newFragment.setArguments(bundle);
						newFragment.show(getActivity()
								.getSupportFragmentManager(), "DatePicker");

					} else {
						// .setText(item.getTitle());
						Calendar cNow = Calendar.getInstance();
						cNow.setTime(new Date(r.getDate()));
						if (item.getTitle()
								.equals(getResources().getString(
										R.string.item_date_tmr))) {
							cNow.set(Calendar.DAY_OF_MONTH,
									cNow.get(Calendar.DAY_OF_MONTH)+1);
						}
						
						
						updateDateTimeAndAlarm(cNow, r);
						
						dateView.setText(item.getTitle());
					}
					return false;
				}
			});

		}

		@Override
		public void finish(String fragmentName, Calendar calResult) {
			Utils.debug("DateOnClickListner", "in  fragmentName");
			/*
			 * calResult - only date fields should be used
			 */
			Calendar cNow = Calendar.getInstance();
			cNow.set(Calendar.YEAR, calResult.get(Calendar.YEAR));
			cNow.set(Calendar.MONTH, calResult.get(Calendar.MONTH));
			cNow.set(Calendar.DAY_OF_MONTH,
					calResult.get(Calendar.DAY_OF_MONTH));

			updateDateTimeAndAlarm(cNow, r);
		}
	}

	class TimeOnClickListner implements OnClickListener, MyDialogResult,
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 745271995085230514L;
		Reminder r;

		TimeOnClickListner(Reminder r) {
			this.r = r;

		}
		 TextView tvTime;
		@Override
		public void onClick(View v) {
			  tvTime = (TextView) v;
			final TimeOnClickListner thisObj = this;
			PopupMenu popup = new PopupMenu(getActivity()
					.getApplicationContext(), v);
			getActivity().getMenuInflater().inflate(R.menu.time,
					popup.getMenu());
			popup.show();
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {

					
					
					if (item.getTitle()
							.equals(getResources().getString(
									R.string.item_time_select))) {
						DialogFragment newFragment = new TimePickerFragment();
						Bundle bundle = new Bundle();
						bundle.putSerializable("objListner", thisObj);
						newFragment.setArguments(bundle);
						newFragment.show(getActivity()
								.getSupportFragmentManager(), "TimePicker");
					}
					else{
						Calendar cNow = Calendar.getInstance();
						cNow.setTimeInMillis(r.getDate());// intialize to old date
						cNow.set(Calendar.MINUTE, 0);
						int HOUR_OF_DAY = DateTimeMenuEnum.getHourOfDay(item.getAlphabeticShortcut()+"");
						cNow.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
						
						updateDateTimeAndAlarm(cNow, r);
						tvTime.setText(item.getTitle());
					}
					return false;
				}
			});

		}

		@Override
		public void finish(String fragmentName, Calendar calResult) {
			Utils.debug("TimeOnClickListner", fragmentName);
			/*
			 * calResult - only hour and min should be used
			 */
			Calendar cNow = Calendar.getInstance();
			cNow.setTimeInMillis(r.getDate());// intialize to old date
			cNow.set(Calendar.HOUR_OF_DAY, calResult.get(Calendar.HOUR_OF_DAY));
			cNow.set(Calendar.MINUTE, calResult.get(Calendar.MINUTE));
			updateDateTimeAndAlarm(cNow, r);
			tvTime.setText(Utils.formatTime(getActivity(), cNow.getTime()));
		}

	}

	private void updateDateTimeAndAlarm(Calendar cNow, Reminder r) {
		if (cNow.getTime().getTime() < System.currentTimeMillis()) {
			Toast.makeText(getActivity(),
					"New Reminder cannot be less than current time",
					Toast.LENGTH_LONG).show();
			return;
		}
		dbHelper.updateNewRemTime(r.getId(), cNow.getTime().getTime());
		Reminder refreshR = dbHelper.getReminder(r.getId());
		alarm.setOnetimeTimer(getActivity(), refreshR);
	}

}
