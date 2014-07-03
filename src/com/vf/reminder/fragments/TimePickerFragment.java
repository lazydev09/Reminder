package com.vf.reminder.fragments;


import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TimePicker;

import com.vf.reminder.fragments.ReminderLstFragment.TimeOnClickListner;
import com.vf.reminder.utils.MyDialogResult;

public class TimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	public MyDialogResult mDialogResult;// callback
	private final static int TIME_PICKER_INTERVAL = 5;
	
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if(bundle!=null){
			mDialogResult =(TimeOnClickListner) bundle.getSerializable("objListner");
		}
		else{
			Fragment fragment = getFragmentManager().findFragmentByTag("NewReminderFragment");
			mDialogResult = (MyDialogResult) fragment;
		}
		
		Log.d("TimePickerFragment", "onCreateDialog " + mDialogResult );
		final Calendar c = Calendar.getInstance();
		int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		Log.d("TimePickerFragment", "onCreateDialog " + hourOfDay );
		return new TimePickerDialog(getActivity(),  this, hourOfDay,
				minute, true);

	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		Log.d("TimePickerFragment", "onTimeSet " + hourOfDay  + " : " +minute);
		if (mDialogResult != null) {
			mDialogResult.finish(TimePickerFragment.class.getSimpleName(),c);
		}
	}
}

