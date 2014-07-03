package com.vf.reminder.fragments;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.DatePicker;

import com.vf.reminder.fragments.ReminderLstFragment.DateOnClickListner;
import com.vf.reminder.utils.MyDialogResult;
import com.vf.reminder.utils.Utils;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	public MyDialogResult mDialogResult;//callback
	

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		Bundle bundle = getArguments();
		if(bundle!=null){
			String reqestFragmentTag = getArguments().getString("ReqestFragmentTag");
			Utils.debug("DatePickerFragment", reqestFragmentTag);
			mDialogResult =(DateOnClickListner) bundle.getSerializable("objListner");
		}
		else{
			Fragment fragment = getFragmentManager().findFragmentByTag("NewReminderFragment");
			mDialogResult = (MyDialogResult) fragment;
			//mDialogResult =(MyDialogResult) getActivity();
		}
	
		
		
		Log.d("DatePicketFragment", "onCreateDialog : " + mDialogResult + " ");
		
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
	
				if( mDialogResult != null ){
							
            mDialogResult.finish(DatePickerFragment.class.getSimpleName(),c);
        }
	}
}
