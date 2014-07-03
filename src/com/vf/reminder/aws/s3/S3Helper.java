package com.vf.reminder.aws.s3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.vf.reminder.MainActivity;
import com.vf.reminder.utils.Utils;

public class S3Helper {
	private static final String TAG = "S3Helper";
	private final String bucketName = "vf2014";
	private final String PATH_SEPARATOR="/";
	private String bucketAbsLoc = "";
	private final AmazonS3 s3;
	
	public S3Helper(String fromUser){
		this.s3 = MainActivity.clientManager.s3();
		bucketAbsLoc = bucketName + PATH_SEPARATOR + fromUser;
	}
	

	
	public Map<String, Date> getBucketDetails() {
		HashMap<String, Date> map = new HashMap<String, Date>();
		Utils.debug(TAG, "listing buckets");
		for (Bucket bucket : s3.listBuckets()) {
			map.put(bucket.getName(), bucket.getCreationDate());
		}
		return map;
	}
	
	public  void deleteBucket(String bucketName) {
		try {
			s3.deleteBucket(bucketName);
		} catch (AmazonServiceException ex) {
			MainActivity.clientManager.wipeCredentialsOnAuthError(ex);
		}
	}

	public String uploadFile(File file) {
		s3.putObject(new PutObjectRequest(bucketAbsLoc, file.getName(), file));
		return bucketAbsLoc+PATH_SEPARATOR+file.getName();
	}
	
	public String uploadFile(InputStream is, String fileName, ObjectMetadata metadata) {
		s3.putObject(new PutObjectRequest(bucketAbsLoc,fileName, is, metadata));
		return bucketAbsLoc+PATH_SEPARATOR+fileName;
	}
	
	public  File downloadFile( String fileKey) {
		try {
			Utils.debug(TAG, "insdie downloadFile bucketAbsLoc : "+ bucketAbsLoc + " fileKey: "+fileKey);
			S3Object object = s3.getObject(bucketAbsLoc, fileKey);
			File file = new File(fileKey);      
			OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
			InputStream reader = new BufferedInputStream(
					   object.getObjectContent());
			int read = -1;

			while ( ( read = reader.read() ) != -1 ) {
			    writer.write(read);
			}

			writer.flush();
			writer.close();
			reader.close();
			return file;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	protected  File read(InputStream stream, String key) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(8196);
			
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = stream.read(buffer)) > 0) {
				baos.write(buffer, 0, length);
			}
			File f = new File(key);
			 FileOutputStream fos = new FileOutputStream(f);
			 fos.write(baos.toByteArray());
		        fos.flush();
		        fos.close();
			return f;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public String getTempURLToDownload(String fileKey) {
		//s3.getObject(new GetObjectRequest(bucketLoc, fileKey));
		System.out.println("Generating pre-signed URL for ."+bucketAbsLoc + " key "+fileKey);
		java.util.Date expiration = new java.util.Date();
		long milliSeconds = expiration.getTime();
		milliSeconds += 1000 * 60 * 10; // Add 10 min
		expiration.setTime(milliSeconds);
		
		GeneratePresignedUrlRequest generatePresignedUrlRequest = 
			    new GeneratePresignedUrlRequest(bucketAbsLoc, fileKey);
		generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
		generatePresignedUrlRequest.setExpiration(expiration);

		URL url = s3.generatePresignedUrl(generatePresignedUrlRequest); 
		
		
		System.out.println("Pre-Signed URL = " + url.toString());
		return url.toString();
	}
}
