package com.vf.reminder.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;


public class PartialRegexInputFilter implements InputFilter {
	private Pattern mPattern;

	public PartialRegexInputFilter(String pattern) {
		mPattern = Pattern.compile(pattern);
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		// TODO Auto-generated method stub
		String textToCheck = dest.subSequence(0, dstart)
				.toString()
				+ source.subSequence(start, end)
				+ dest.subSequence(dend, dest.length())
						.toString();
		
		Matcher matcher = mPattern.matcher(textToCheck);

		// Entered text does not match the pattern
		if (!matcher.matches()) {
			//Log.d("REGEX NOT MATCHED",textToCheck);
			// It does not match partially too
			return "";
			

		}

		return null;
	}

}
