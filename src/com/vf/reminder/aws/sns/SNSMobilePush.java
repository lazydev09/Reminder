package com.vf.reminder.aws.sns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.DeleteEndpointRequest;
import com.amazonaws.services.sns.model.DeletePlatformApplicationRequest;
import com.amazonaws.services.sns.model.Endpoint;
import com.amazonaws.services.sns.model.GetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationRequest;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vf.reminder.MainActivity;
import com.vf.reminder.aws.ddb.DBResponseHelper;
import com.vf.reminder.utils.Utils;

public class SNSMobilePush {

	private static final String TAG = "SNSMobilePush";

	public static enum Platform {
		// Apple Push Notification Service
		APNS,
		// Sandbox version of Apple Push Notification Service
		APNS_SANDBOX,
		// Amazon Device Messaging
		ADM,
		// Google Cloud Messaging
		GCM
	}

	public static final String PLATFORM_APPLICATION_ARN = "arn:aws:sns:ap-southeast-1:332022304140:app/GCM/Reminder";

	private final static ObjectMapper objectMapper = new ObjectMapper();

	private final AmazonSNS snsClient;

	public SNSMobilePush(AmazonSNS sns) {
		snsClient = sns;
	}

	private CreatePlatformApplicationResult createPlatformApplication(
			String applicationName, Platform platform, String principal,
			String credential) {
		CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("PlatformPrincipal", principal);
		attributes.put("PlatformCredential", credential);
		platformApplicationRequest.setAttributes(attributes);
		platformApplicationRequest.setName(applicationName);
		platformApplicationRequest.setPlatform(platform.name());
		return snsClient.createPlatformApplication(platformApplicationRequest);
	}

	public CreatePlatformEndpointResult createPlatformEndpoint(
			String customData, String platformToken, String applicationArn) {
		Utils.debug("ENDPOINT", "customData : " + customData
				+ " : platformToken : " + platformToken + " : "
				+ applicationArn);
		CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
		platformEndpointRequest.setCustomUserData(customData);
		platformEndpointRequest.setToken(platformToken);
		platformEndpointRequest.setPlatformApplicationArn(applicationArn);
		return snsClient.createPlatformEndpoint(platformEndpointRequest);
	}

	public PublishRequest getPublishReq(String endpointArn, Platform platform,
			String notificationMessage) {
		PublishRequest publishRequest = new PublishRequest();
		Map<String, String> messageMap = new HashMap<String, String>();
		String message;
		//messageMap.put("default", notificationMessage);
		messageMap.put(platform.name(), formatAndroidMessage(notificationMessage));
		// For direct publish to mobile end points, topicArn is not relevant.
		
		message = jsonify(messageMap);

		// Display the message that will be sent to the endpoint/
		Utils.debug("GCM_F","FInal message sent"+message);

		publishRequest.setMessage(message);
		publishRequest.setTargetArn(endpointArn);
		publishRequest.setMessageStructure("json");
		
		return publishRequest;
	}
	
	private Map<String, String> getData(String message) {
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("message", message);
        return payload;
    }
	
	private String formatAndroidMessage(String message) {
        Map<String, Object> androidMessageMap = new HashMap<String, Object>();
        androidMessageMap.put("collapse_key", "Alarm");
        androidMessageMap.put("data", getData(message));
        androidMessageMap.put("delay_while_idle", true);
        androidMessageMap.put("time_to_live", 125);
        androidMessageMap.put("dry_run", false);
        return jsonify(androidMessageMap);
    }
	
	public void deletePlatformEndpoint(String endpointArn){
		DeleteEndpointRequest request = new DeleteEndpointRequest();
		request.setEndpointArn(endpointArn);
		
		snsClient.deleteEndpoint(request);
	}

	private void deletePlatformApplication(String applicationArn) {
		DeletePlatformApplicationRequest request = new DeletePlatformApplicationRequest();
		request.setPlatformApplicationArn(applicationArn);
		snsClient.deletePlatformApplication(request);
	}

	private static String jsonify(Object message) {
		try {
			return objectMapper.writeValueAsString(message);
		} catch (Exception e) {
			e.printStackTrace();
			throw (RuntimeException) e;
		}
	}
	
	
	public boolean publishNotification(String endpointArn, String message){
		try{
			Utils.debug("TAG", "Publishing message ::: "+message);
			PublishRequest request = getPublishReq(endpointArn, SNSMobilePush.Platform.GCM, message);
			PublishResult result = snsClient.publish(request);
			Utils.debug(TAG, "Resulr"+result.toString());
			
			return true;
		}catch(AmazonServiceException ex){
			MainActivity.clientManager.wipeCredentialsOnAuthError(ex);
			DBResponseHelper<String> responseHelper = new DBResponseHelper<String>();
			responseHelper.setSuccess(false);
			responseHelper.setErrMessage("Service Not available, Please try again later");
			return false;	
		}
		catch(AmazonClientException ex){
			DBResponseHelper<String> responseHelper = new DBResponseHelper<String>();
			responseHelper.setSuccess(false);
			responseHelper.setErrMessage("Service Not available, Please try again later");
			return false;	
		}
	}
	
	public void getAllEndpointAttributes(String delegationToken){
		GetEndpointAttributesRequest request = new GetEndpointAttributesRequest();
		ListEndpointsByPlatformApplicationRequest r1 = new ListEndpointsByPlatformApplicationRequest();
		r1.setPlatformApplicationArn("arn:aws:sns:ap-southeast-1:332022304140:app/GCM/Reminder");
	
		ListEndpointsByPlatformApplicationResult re = snsClient.listEndpointsByPlatformApplication(r1);
		List<Endpoint> lstArn = re.getEndpoints();
		Utils.debug(TAG, "lstArn "+lstArn.size());
		for(Endpoint e : lstArn){
			Utils.debug(TAG, "e "+e.getEndpointArn());	
		}
	}

	public class PublishMessageTask extends
			AsyncTask<PublishRequest, Void, PublishResult> {

		@Override
		protected PublishResult doInBackground(PublishRequest... params) {
			// TODO Auto-generated method stub
			return snsClient.publish(params[0]);
		}

	}
}
