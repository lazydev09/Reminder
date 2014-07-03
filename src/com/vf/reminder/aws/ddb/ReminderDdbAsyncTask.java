package com.vf.reminder.aws.ddb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.vf.reminder.MainActivity;
import com.vf.reminder.aws.s3.S3Helper;
import com.vf.reminder.aws.sns.SNSMobilePush;
import com.vf.reminder.db.FriendReminderDBHelper;
import com.vf.reminder.db.SqliteDBHelper;
import com.vf.reminder.db.tables.Reminder;
import com.vf.reminder.db.tables.User;
import com.vf.reminder.utils.NotificationMessageHelper;
import com.vf.reminder.utils.Utils;

public class ReminderDdbAsyncTask  extends AsyncTask<Reminder, Void, List<DBResponseHelper<Reminder>>>{
	
	private static final String TAG = "ReminderDdbAsyncTask";
	
	public static enum ReminderTaskEnum{
		SEND_FRIEND_REMINDERS, READ_SENT_FRIEND_REMINDERS;
	}
	
	ReminderTaskEnum taskEnum;
	String friendId;
	Context context;
	
	public ReminderDdbAsyncTask(ReminderTaskEnum taskEnum, Context context){
		this.taskEnum = taskEnum;
		this.context = context;
	}
	
	public ReminderDdbAsyncTask(ReminderTaskEnum taskEnum, String friendId){
		this.taskEnum = taskEnum;
		this.friendId=friendId;
	}

	@Override
	protected List<DBResponseHelper<Reminder>> doInBackground(Reminder... params) {
		// TODO Auto-generated method stub
		switch(taskEnum){
		case SEND_FRIEND_REMINDERS : {
			
			return sendFriendReminder(params);
			
		}
		
		
		default : return null;
		}
	}
	
	
	private List<DBResponseHelper<Reminder>> sendFriendReminder(Reminder... params){
		
		//save to db
		//get s3 id
		//finally send notification
		List<DBResponseHelper<Reminder>> lstResponse = new ArrayList<DBResponseHelper<Reminder>>();
		DBResponseHelper<Reminder[]> reponse = new DBResponseHelper<Reminder[]>();
		try{
			
	
		
		
		for(Reminder r : params){
			DdbManager db = new DdbManager();
			User toUser = db.getUser(r.getToUser());
			
			String toArn = toUser.getArn();
			String s3Key = "";
			if(Utils.AUDIO == r.getType()){
				S3Helper s3  = new S3Helper(r.getFromUser().replaceAll("\\+", ""));
				File f = new File(r.getFileLoc());
				s3Key = s3.uploadFile(f);
				Utils.debug(TAG, "Upload to s3 succesful" +s3Key);
			}
			
		
			r.setFileLoc(s3Key);
			r.setStatus(SqliteDBHelper.INIT);
			r.setRemoteId(null);
			String remoteId = db.saveFriendReminder(r);
			r.setRemoteId(remoteId);
			
			
			NotificationMessageHelper msgHelper = new NotificationMessageHelper();
			msgHelper.setIntent(NotificationMessageHelper.INTENT_REM);
			msgHelper.setFrm(r.getFromUser());
			msgHelper.setType(r.getType());
			msgHelper.setMsg(r.getMessage());
			msgHelper.setS3Key(s3Key);
			msgHelper.setTime(r.getDate());
			
			String jsonMsg = new Gson().toJson(msgHelper,NotificationMessageHelper.class);
			Utils.debug("TAG", "jsonMsgjsonMsgjsonMsg "+jsonMsg);
			SNSMobilePush sns  = new SNSMobilePush(MainActivity.clientManager.sns());
			boolean sent = sns.publishNotification(toArn, jsonMsg);
			
			DBResponseHelper<Reminder> result = new DBResponseHelper<Reminder>();
			if(sent){
				
				r.setFileLoc(s3Key);
				result.setSuccess(true);
				result.setT(r);
				result.setErrMessage("");
				
			}
			else{
				r.setStatus(SqliteDBHelper.FAILED);	
				result.setSuccess(false);
				result.setT(r);
				result.setErrMessage("Failed");
			}
			lstResponse.add(result);
			//sync data
			try{
				FriendReminderDBHelper dbHelper = new FriendReminderDBHelper(context);
				dbHelper.insertFriendReminder(r);
			}catch(Exception ex){
				ex.printStackTrace();
				
				//ignore if failed tosync data - show success to customer as it is delived
			}
			
		
		}
		return lstResponse;
		
	}catch(Exception ex){
		ex.printStackTrace();
		DBResponseHelper<Reminder> result = new DBResponseHelper<Reminder>();
		result.setSuccess(false);
		result.setErrMessage(ex.getMessage());
		lstResponse.add(result);
		return lstResponse;
		}
	}

}
