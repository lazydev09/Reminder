package com.vf.reminder.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import com.vf.reminder.aws.s3.DownloadFileTask;


public class AudioHelper {
	private static final String LOG_TAG = "AudioHelper";
	private  String mFileName ;
	private File fileDir;
	Context context;
	
	private MediaRecorder mRecorder = null;

	private MediaPlayer mPlayer = null;
	
	public AudioHelper(Context context) {
		this.context=context;
		mFileName = getTempFile(context, "audioFile");
		Log.d(LOG_TAG, "creating new file "+mFileName);
		//mFileName += System.currentTimeMillis()+"/audMsg.3gp";
	}
	
	
	
	public AudioHelper(Context context, boolean playOnly) {
		if(!playOnly){
			//not supported
		}
		//fileDir =  context.getDir("Audio", Context.MODE_PRIVATE);
		
				
	}
	
	public void onPlayOnly(boolean start, String fileName) {
		
		if(fileName.startsWith("vf2014")){
			downloadFile(fileName, start);
		}
		else{
		mFileName = new File(fileName).getAbsolutePath();
		Log.d(LOG_TAG, "reading  new file from "+mFileName);
		
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
		}
	}
	
	
	public String getFileName(){
		return mFileName;
	}

	public void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	public void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	private void startPlaying() {
		
		mPlayer = new MediaPlayer();
		try {
			Log.e(LOG_TAG, "startPlaying() called" +mFileName);
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	public void stopPlaying() {

		mPlayer.release();
		mPlayer = null;
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			Log.e(LOG_TAG, "prepare() " +mFileName);
			mRecorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "prepare() failed");
		}
		
		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.reset();
		mRecorder.release();
		mRecorder = null;
	}

	

		

	public void onPause() {
        
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".3gp";
	
	public String getTempCacheFile(Context context, String url) {
		File file = context.getCacheDir();
		try {
			String fileName = "AudMsg";
			
			file = File.createTempFile(fileName, AUDIO_RECORDER_FILE_EXT_WAV,
					file);
			return file.getAbsolutePath();
		}

		catch (IOException e) {
			// Error while creating file
			e.printStackTrace();
			return file.getAbsolutePath() + "/" + System.currentTimeMillis()
					+ AUDIO_RECORDER_FILE_EXT_WAV;
		}

	}
	
	public String getTempFile(Context context, String url) {
		File file = context.getDir("Audio", Context.MODE_PRIVATE);
		try {
			String fileName = "AudMsg";
			file = File.createTempFile(fileName, AUDIO_RECORDER_FILE_EXT_WAV,
					file);
			return file.getAbsolutePath();
		}

		catch (IOException e) {
			// Error while creating file
			e.printStackTrace();
			return file.getAbsolutePath() + "/" + System.currentTimeMillis()
					+ AUDIO_RECORDER_FILE_EXT_WAV;
		}

	}
	
	 File audioFile = null;
	
	private void downloadFile(String s3Key,final boolean start ){
		Utils.debug(LOG_TAG, "downloadFile "+s3Key);
		new DownloadFileTask(){

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				Utils.debug(LOG_TAG, "onPostExecute "+result);
				if(result==null){
					return ;
				}
				MediaPlayer mediaPlayer = new MediaPlayer();
				if (start) {
					
					try {
						
						mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
						mediaPlayer.setDataSource(result);
						mediaPlayer.prepare();
						mediaPlayer.start();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // might take long! (for buffering, etc)
					
				} else {
					mediaPlayer.stop();
					mediaPlayer.release();
					
					
				}
			}
			
		}.execute(s3Key);
		
	}
	
	
}

