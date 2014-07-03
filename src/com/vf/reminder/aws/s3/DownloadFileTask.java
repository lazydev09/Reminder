package com.vf.reminder.aws.s3;

import java.io.File;

import android.os.AsyncTask;

public class DownloadFileTask extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		
		try{
			String s3key = params[0];
			String[] keyArr = s3key.split("/");
			String userKey = keyArr[1];
			String fileKey = keyArr[2];
			S3Helper s3Helper = new S3Helper(userKey);
			 s3Helper.downloadFile(fileKey);
			return s3Helper.getTempURLToDownload(fileKey);
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		
	}

}
