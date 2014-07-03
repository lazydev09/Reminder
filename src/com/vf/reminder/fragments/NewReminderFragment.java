package com.vf.reminder.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.vf.reminder.NewLocalReminder;
import com.vf.reminder.R;
import com.vf.reminder.SlidingMenuActivity;
import com.vf.reminder.alarm.AlarmHelper;
import com.vf.reminder.aws.ddb.DBResponseHelper;
import com.vf.reminder.aws.ddb.ReminderDdbAsyncTask;
import com.vf.reminder.aws.ddb.ReminderDdbAsyncTask.ReminderTaskEnum;
import com.vf.reminder.db.RemDBObjHelper;
import com.vf.reminder.db.RemDBObjHelper.Builder;
import com.vf.reminder.db.ReminderDBHelper;
import com.vf.reminder.db.tables.Reminder;
import com.vf.reminder.utils.AudioHelper;
import com.vf.reminder.utils.MyDialogResult;
import com.vf.reminder.utils.StateHelper;
import com.vf.reminder.utils.Utils;



public class NewReminderFragment extends Fragment implements MyDialogResult {

	private static final String TAG = "NewReminderActivity";
	ViewGroup container;
	VideoView videoView;
	ImageView imgRecBtn;
	EditText textMsg;
	String dateSel;
	String timeSel;
	Builder builder = new RemDBObjHelper.Builder();
	TextView txtDate;
	TextView txtTime;
	// audio
	AudioHelper audHelper;
	ProgressBar pbAudBar;
	private final int MAX_TIME = 60;
	private Handler mProgressHandler = new Handler();
	LinearLayout audView;
	ImageButton imgPlayBtn;
	TextView tvProgressText;
	// phones
	ArrayList<String> selContacts = new ArrayList<String>();
	boolean isLocal = true;
	FriendDetailsFragment friendRemFragment;
	//booleans
	boolean isForward = false;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_new_reminder, container,
				false);
		Button btnSendRem = (Button) view.findViewById(R.id.btnSendRem);
		Log.d(TAG, getActivity().getClass().getSimpleName());
		if (getActivity().getClass().getSimpleName()
				.equalsIgnoreCase(SlidingMenuActivity.class.getSimpleName())) {
			Log.d(TAG, "Called from Sliding Menu Activity");
			isLocal = false;
			btnSendRem.setText("Send Reminder");

		}
		Intent intent = getActivity().getIntent();
		if(intent.getExtras()!=null){
		Object objForward = intent.getExtras().get(Utils.TYPE);
		if(Utils.FORWARD.equalsIgnoreCase(""+objForward)){
			//forward
			isForward = true;
		}
		}
		if (getActivity().getClass().getSimpleName()
				.equalsIgnoreCase(NewLocalReminder.class.getSimpleName())) {
			Log.d(TAG, "Called from New Local Reminder Activity");
			isLocal = true;
			btnSendRem.setText("Set Reminder");
		}
		
		
		
		ImageButton imageBtnAud = (ImageButton) view.findViewById(R.id.imgAud);
		
		ImageButton imageBtnText = (ImageButton) view
				.findViewById(R.id.imgText);

		textMsg = (EditText) view.findViewById(R.id.etMsg);
		txtDate = (TextView) view.findViewById(R.id.tvTime);
		txtTime = (TextView) view.findViewById(R.id.tvDate);

		if (!isLocal) {
			// frame list
			friendRemFragment = new FriendDetailsFragment();
			FragmentManager fragmentManager = getActivity()
					.getSupportFragmentManager();
			fragmentManager
					.beginTransaction()
					.replace(R.id.frd_details_frame, friendRemFragment,
							"FriendDetailsFragment").commit();
		}

		videoView = (VideoView) view.findViewById(R.id.videoView1);
		audView = (LinearLayout) view.findViewById(R.id.layoutAudio);
		imgRecBtn = (ImageView) view.findViewById(R.id.imgAudioRec);
		pbAudBar = (ProgressBar) view.findViewById(R.id.pbAud);
		imgPlayBtn = (ImageButton) view.findViewById(R.id.imgAudioPlay);
		tvProgressText = (TextView) view
				.findViewById(R.id.voicerec_progressText);
		
		MediaController mediaController = new MediaController(getActivity());
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);

		//dfault text message
		builder.type(Utils.TEXT);
		
		// audio
		imageBtnAud.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				tvProgressText = (TextView) audView
						.findViewById(R.id.voicerec_progressText);

				tvProgressText.setVisibility(View.VISIBLE);
				tvProgressText.setText("60");

				textMsg.setVisibility(View.GONE);
				videoView.setVisibility(View.GONE);
				audView.setVisibility(View.VISIBLE);
				builder.type(Utils.AUDIO);
				takeAudioReminder();

			}
		});

		// play btn
		ImageView audImgPlay = (ImageView) view.findViewById(R.id.imgAudioPlay);
		audImgPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				textMsg.setVisibility(View.GONE);
				videoView.setVisibility(View.GONE);
				audView.setVisibility(View.VISIBLE);
				audHelper.onPlay(true);

			}
		});

		

		// text
		imageBtnText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				videoView.setVisibility(View.GONE);
				audView.setVisibility(View.GONE);
				textMsg.setVisibility(View.VISIBLE);
				builder.type(Utils.TEXT);

			}
		});

		// date events
		// final TextView txtDate = (TextView) view.findViewById(R.id.tvDate);
		txtDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(getActivity(), v);
				getActivity().getMenuInflater().inflate(R.menu.date,
						popup.getMenu());
				// showDialog();
				popup.show();

				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Log.d("DEDE", item.getTitle() + " : "
								+ R.string.item_date_select + " item id "
								+ item.getItemId());

						if (item.getTitle().equals(
								getResources().getString(
										R.string.item_date_select))) {

							DialogFragment newFragment = new DatePickerFragment();
							newFragment.show(getActivity()
									.getSupportFragmentManager(), "DatePicker");

						} else {
							txtDate.setText(item.getTitle());
						}
						return false;
					}
				});

			}
		});

		// time events
		// final TextView txtTime = (TextView) view.findViewById(R.id.tvTime);
		txtTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(getActivity()
						.getApplicationContext(), v);
				getActivity().getMenuInflater().inflate(R.menu.time,
						popup.getMenu());
				popup.show();
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						txtTime.setText(item.getTitle());
						if (item.getTitle().equals(
								getResources().getString(
										R.string.item_time_select))) {
							DialogFragment newFragment = new TimePickerFragment();
							newFragment.show(getActivity()
									.getSupportFragmentManager(), "TimePicker");
						}
						return false;
					}
				});

			}
		});

		// priority
		final TextView txtPri = (TextView) view.findViewById(R.id.tvPriority);
		txtPri.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(getActivity()
						.getApplicationContext(), v);
				getActivity().getMenuInflater().inflate(R.menu.priority,
						popup.getMenu());
				popup.show();
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						txtPri.setText(item.getTitle());
						return false;
					}
				});

			}
		});
		// btn event

		btnSendRem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(TAG, "selected date : " + txtDate.getText().toString());
				Log.d(TAG, "selected time : " + txtTime.getText().toString());
				Log.d(TAG, "selected msg : " + textMsg.getText().toString());

				if (getResources().getString(R.string.item_date_today)
						.equalsIgnoreCase(txtDate.getText().toString())) {
					builder.addToday();
				} else if (getResources().getString(R.string.item_date_tmr)
						.equalsIgnoreCase(txtDate.getText().toString())) {
					builder.addTomorrow();
				} else {
					// date set in call back method
				}
				// time
				if (getResources().getString(R.string.item_time_mor)
						.equalsIgnoreCase(txtTime.getText().toString())) {
					builder.addMorning();
				} else if (getResources().getString(R.string.item_time_aft)
						.equalsIgnoreCase(txtTime.getText().toString())) {
					builder.addAfterNoon();
				} else if (getResources().getString(R.string.item_time_eve)
						.equalsIgnoreCase(txtTime.getText().toString())) {
					builder.addEvening();
				} else if (getResources().getString(R.string.item_time_nig)
						.equalsIgnoreCase(txtTime.getText().toString())) {
					builder.addNight();
				} else {
					// time set in call back method
				}

				// type
				if (Utils.AUDIO.equalsIgnoreCase(builder.getType())) {
					builder.fileLocation(audHelper.getFileName());
				}
				
				builder.message(textMsg.getText().toString());
				builder.time();
				builder.localId(0);
				builder.remote_id("TODO");
				builder.fromUser("ME");
				RemDBObjHelper objHelper = builder.build();
				Log.d(TAG, "FInal Obj " + objHelper.toString());
				if (isLocal) {
					AlarmHelper alarm = new AlarmHelper();
					ReminderDBHelper dbHelper = new ReminderDBHelper(
							getActivity());

					long result = dbHelper.insertReminder(objHelper);
					Log.d(TAG, "After updating , DB result" + result);
					objHelper.setId(result);
					alarm.setOnetimeTimer(
							getActivity().getApplicationContext(), objHelper);
					Intent intent = new Intent();
					intent.putExtra("DATA", objHelper);
					getActivity().setResult(getActivity().RESULT_OK, intent);
					getActivity().finish();

				} else {

					if (selContacts.size() == 0) {
						Toast.makeText(
								getActivity(),
								"No friend added, You can drag and drop multiple friends from side bar",
								Toast.LENGTH_LONG).show();
						return;
					}
					if(objHelper.getType()==Utils.TEXT){
						if(objHelper.getMsg()==null || objHelper.getMsg().isEmpty()){
							Toast.makeText(
									getActivity(),
									"Please enter message or choose audio reminder",
									Toast.LENGTH_LONG).show();
							return;
						}
					}
					
					
					Log.d(TAG, "no of friends selected " + selContacts.size());
					Reminder[] remArr = new Reminder[selContacts.size()];
					for(int i=0; i< selContacts.size();i++){
						Reminder r = new Reminder();
						r.setId( objHelper.getId());
						r.setDate(objHelper.getTime());
						r.setFileLoc(objHelper.getFile_loc());
						r.setFromUser(StateHelper.getUser(getActivity()).getMobile());
						r.setMessage(objHelper.getMsg());
						r.setRemoteId("");
						r.setToUser(selContacts.get(i));
						r.setType(objHelper.getType());
						remArr[0] = r;
						
					}
					
					sendFriendReminder(remArr);
					

				}
			}
		});

		// linear layout
		LinearLayout linearLayoutPhotos = (LinearLayout) view
				.findViewById(R.id.linearLayoutPhotos);
		view.setOnDragListener(new MyDragListener());

		return view;
	}
	
	boolean isRecording = false;

	private void takeAudioReminder() {

		imgRecBtn.setVisibility(View.VISIBLE);
		audHelper = new AudioHelper(getActivity().getApplicationContext());
		imgRecBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!isRecording) {
					startRecording();
				} else {
					// stop recording
					stopRecording();
				}

			}
		});

	}
	
	
	private void startRecording(){
		isRecording = true;
		audHelper.onRecord(isRecording);
		tvProgressText =(TextView) audView.findViewById(R.id.voicerec_progressText);
		tvProgressText.setVisibility(View.VISIBLE);
		tvProgressText.setText("60");
		pbAudBar.setProgress(0);
		pbAudBar.setMax(MAX_TIME);
		imgPlayBtn.setVisibility(View.GONE);
		showPBAnimation();
		
	}
	
	private void stopRecording(){
		// stop recording
		cdTimer.cancel();
		tvProgressText =(TextView) audView.findViewById(R.id.voicerec_progressText);
		tvProgressText.setVisibility(View.GONE);
		isRecording = false;
		audHelper.onRecord(isRecording);
		imgPlayBtn.setVisibility(View.VISIBLE);
		tvProgressText.setVisibility(View.GONE);
	}

	
	private void sendFriendReminder(Reminder[] remArr) {
		//http
		new ReminderDdbAsyncTask(ReminderTaskEnum.SEND_FRIEND_REMINDERS,getActivity().getApplicationContext()){

			@Override
			protected void onPostExecute(List<DBResponseHelper<Reminder>> result) {
				boolean isSuccess = false;
				for(DBResponseHelper<Reminder> arr : result){
					
					if(arr.isSuccess()){
						/*
						 * for time being just showing success if at least one message sent is success
						 */
						isSuccess = true;
						
					}
					else{
					
						
					}
				}
				
				if(isSuccess){
					Intent intent = new Intent();

					getActivity().setResult(getActivity().RESULT_OK, intent);
					getActivity().finish();
				}
				else{
					Toast.makeText(getActivity(), "Failed to send reminder. Service No available, Please try again later ", Toast.LENGTH_LONG).show();
					Intent intent = new Intent();

					getActivity().setResult(getActivity().RESULT_CANCELED, intent);
					getActivity().finish();
					
				}
				
			}

		
		
			
			
		}.execute(remArr);
	}
	
	
	class MyDragListener implements OnDragListener {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			Log.d("DROP", "insdie MyDragListener : " + event.getAction());
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				return true;
			case DragEvent.ACTION_DRAG_ENTERED: {
				// v.setColorFilter(Color.GREEN);

				// Invalidate the view to force a redraw in the new tint

				return true;

			}
			case DragEvent.ACTION_DRAG_LOCATION:

				return true;

			case DragEvent.ACTION_DRAG_EXITED:

			
				return true;

			case DragEvent.ACTION_DROP:
				Log.d("DROP", "insdie action drop");
				// Gets the item containing the dragged data
				ClipData.Item item = event.getClipData().getItemAt(0);
				ClipData.Item item1 = event.getClipData().getItemAt(1);

				// Gets the text data from the item.
				String phonoNo = item.getText().toString();
				selContacts.add(phonoNo);
				CharSequence item2 = item1.getText();
				Log.d("DROP", "insdie action drop" + item2);
				Log.d("DROP", "insdie action drop" + phonoNo);
				// Displays a message containing the dragged data.
				Toast.makeText(getActivity(),
						"Dragged data is " + phonoNo + " :: " + item2,
						Toast.LENGTH_LONG).show();
				// send mobile to fragment and refresh the list
				friendRemFragment.setMobileNo(phonoNo);

				View view = (View) event.getLocalState();
				Object viewtag = view.getTag();
				// ViewGroup owner = (ViewGroup) view.getParent();
				// owner.removeView(view);
				ImageView view1 = (ImageView) view;
				Bitmap bitmap = ((BitmapDrawable) view1.getDrawable())
						.getBitmap();
				if(viewtag!=null || !"".equals(viewtag)){
					bitmap = Utils.mark(bitmap, viewtag + "");
				}
				
				ImageView newView = new ImageView(getActivity());
				newView.setAdjustViewBounds(true);

				int wpx = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
								.getDisplayMetrics());
				newView.setMaxWidth(wpx);

				int hpx = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
								.getDisplayMetrics());
				newView.setMaxHeight(hpx);
				newView.setImageBitmap(bitmap);
				newView.setOnClickListener(new DynamicImageOnClickListner());

				LinearLayout ll = (LinearLayout) v
						.findViewById(R.id.linearLayoutPhotos);
				int childcount = ll.getChildCount();
				for (int i = 0; i < childcount; i++) {
					View vc = ll.getChildAt(i);
					Log.i("DROP", "lllll : " + vc);
				}
				ll.addView(newView);
				// container.addView(newView);

				view.setVisibility(View.VISIBLE);
				return true;

			case DragEvent.ACTION_DRAG_ENDED:

				if (event.getResult()) {
					Toast.makeText(getActivity(), "The drop was handled.",
							Toast.LENGTH_LONG).show();

				} else {
					Toast.makeText(getActivity(), "The drop didn't work.",
							Toast.LENGTH_LONG).show();

				}
				;

			}
			return true;

		}
	}
	
	public void handleGoBack() {
		Log.d(TAG, "In jandle gp naclg");
		Intent intent = new Intent();
		intent.putExtra("ONBACK", true);
		getActivity().setResult(getActivity().RESULT_OK, intent);
		getActivity().finish();

	}

	private boolean uploadFile(Reminder r) {
		return false;
	}

	@Override
	public void finish(String fragmentName, Calendar calResult) {
		Log.d(TAG,
				"inside finsh of" + NewReminderFragment.class.getSimpleName()
						+ " fragmentName : " + fragmentName);
		if (DatePickerFragment.class.getSimpleName().equalsIgnoreCase(
				fragmentName)) {

			builder.addDate(calResult.get(Calendar.DAY_OF_MONTH),
					calResult.get(Calendar.MONTH), calResult.get(Calendar.YEAR));
			txtDate.setText(Utils.formatDate(getActivity(),
					calResult.getTime()));
		}
		if (TimePickerFragment.class.getSimpleName().equalsIgnoreCase(
				fragmentName)) {

			Log.d(TAG,
					"received time in finish () "
							+ calResult.get(Calendar.HOUR_OF_DAY) + ": "
							+ calResult.get(Calendar.MINUTE));
			builder.addTime(calResult.get(Calendar.HOUR_OF_DAY),
					calResult.get(Calendar.MINUTE));
			txtTime.setText(Utils.formatTime(getActivity(),
					calResult.getTime()));

		}

	}
	
	private void handleForward(Reminder rForward){
		//get type
	}
	
	CountDownTimer cdTimer = null;
	
	public void showPBAnimation() {
		cdTimer = new CountDownTimer(60000, 1000) {
			Random rand = new Random();
			int freq = rand.nextInt(6);
			int ctr = 0;

			@Override
			public void onTick(long millisUntilFinished) {
				/*
				 * Call to this method will take random inputs(freq) to fill the
				 * relative layout with the freq multiplied to height of
				 * textview. And hence showing a different height each time.
				 */

				// createStatisticalGraphs(6, 6 - freq, mProgressTxv);

				/* Continous increment in the progress bar's level */

				mProgressHandler.post(new Runnable() {
					public void run() {
						pbAudBar.setProgress(++ctr);
						tvProgressText =(TextView) audView.findViewById(R.id.voicerec_progressText);
						tvProgressText.setText(""+(60 - pbAudBar.getProgress()));
					}
				});

			}

			@Override
			public void onFinish() {
				if (cdTimer != null) {
					// pbAudView.setVisibility(View.GONE);
					stopRecording();
					cdTimer.cancel();
				}

			}
		};
		cdTimer.start();
	}
	

	class DynamicImageOnClickListner implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}

	}
}
