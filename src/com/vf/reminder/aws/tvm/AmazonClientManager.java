package com.vf.reminder.aws.tvm;


import android.content.SharedPreferences;
import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.tvmclient.AmazonSharedPreferencesWrapper;
import com.amazonaws.tvmclient.AmazonTVMClient;
import com.amazonaws.tvmclient.Response;

/**
 * This class is used to get clients to the various AWS services. Before
 * accessing a client the credentials should be checked to ensure validity.
 */
public class AmazonClientManager {
	private static final String LOG_TAG = "AmazonClientManager";

	private AmazonS3Client s3Client = null;
	private AmazonSQSClient sqsClient = null;
	private AmazonSNSClient snsClient = null;
	private AmazonDynamoDBClient ddbClient = null;
	private SharedPreferences sharedPreferences = null;

	public AmazonClientManager(SharedPreferences settings) {
		this.sharedPreferences = settings;
	}

	public AmazonS3Client s3() {
		validateCredentials();
		return s3Client;
	}

	public AmazonSQSClient sqs() {
		validateCredentials();
		return sqsClient;
	}

	public AmazonDynamoDBClient ddb() {
		validateCredentials();
		return ddbClient;
	}

	public AmazonSNSClient sns() {
		validateCredentials();
		return snsClient;
	}

	public boolean hasCredentials() {
		return PropertyLoader.getInstance().hasCredentials();
	}

	public Response validateCredentials() {

		Response ableToGetToken = Response.SUCCESSFUL;

		if (AmazonSharedPreferencesWrapper
				.areCredentialsExpired(this.sharedPreferences)) {

			synchronized (this) {

				if (AmazonSharedPreferencesWrapper
						.areCredentialsExpired(this.sharedPreferences)) {

					Log.i(LOG_TAG, "Credentials were expired.");

					AmazonTVMClient tvm = new AmazonTVMClient(this.sharedPreferences,
							PropertyLoader.getInstance().getTokenVendingMachineURL(),
							PropertyLoader.getInstance().useSSL());

					ableToGetToken = tvm.anonymousRegister();

					if (ableToGetToken.requestWasSuccessful()) {

						ableToGetToken = tvm.getToken();

						if (ableToGetToken.requestWasSuccessful()) {
							Log.i(LOG_TAG, "Creating New Credentials.");
							initClients();
						}
					}
				}
			}

		} else if (s3Client == null || sqsClient == null || ddbClient == null
				|| snsClient == null) {

			synchronized (this) {

				if (s3Client == null || sqsClient == null || ddbClient == null
						|| snsClient == null) {

					Log.i(LOG_TAG, "Creating New Credentials.");
					initClients();
				}
			}
		}

		return ableToGetToken;
	}

	private void initClients() {
		AWSCredentials credentials = AmazonSharedPreferencesWrapper
				.getCredentialsFromSharedPreferences(this.sharedPreferences);

		Region region = Region.getRegion(Regions.AP_SOUTHEAST_1); 
        
        s3Client = new AmazonS3Client( credentials );
	    s3Client.setRegion(region);
	    
	    sqsClient = new AmazonSQSClient( credentials );
	    sqsClient.setRegion(region);
	    
	    ddbClient = new AmazonDynamoDBClient( credentials );
	    ddbClient.setRegion(region);
	    
	    snsClient = new AmazonSNSClient( credentials );
	    snsClient.setRegion(region);		
	}

	public void clearCredentials() {

		synchronized (this) {
			
			AmazonSharedPreferencesWrapper.wipe(this.sharedPreferences);

			s3Client = null;
			sqsClient = null;
			ddbClient = null;
			snsClient = null;
		}
	}

	public boolean wipeCredentialsOnAuthError(AmazonServiceException ex) {
		if (
		// STS
		// http://docs.amazonwebservices.com/STS/latest/APIReference/CommonErrors.html
		ex.getErrorCode().equals("IncompleteSignature")
				|| ex.getErrorCode().equals("InternalFailure")
				|| ex.getErrorCode().equals("InvalidClientTokenId")
				|| ex.getErrorCode().equals("OptInRequired")
				|| ex.getErrorCode().equals("RequestExpired")
				|| ex.getErrorCode().equals("ServiceUnavailable")

				// For S3
				// http://docs.amazonwebservices.com/AmazonS3/latest/API/ErrorResponses.html#ErrorCodeList
				|| ex.getErrorCode().equals("AccessDenied")
				|| ex.getErrorCode().equals("BadDigest")
				|| ex.getErrorCode().equals("CredentialsNotSupported")
				|| ex.getErrorCode().equals("ExpiredToken")
				|| ex.getErrorCode().equals("InternalError")
				|| ex.getErrorCode().equals("InvalidAccessKeyId")
				|| ex.getErrorCode().equals("InvalidPolicyDocument")
				|| ex.getErrorCode().equals("InvalidToken")
				|| ex.getErrorCode().equals("NotSignedUp")
				|| ex.getErrorCode().equals("RequestTimeTooSkewed")
				|| ex.getErrorCode().equals("SignatureDoesNotMatch")
				|| ex.getErrorCode().equals("TokenRefreshRequired")

				// SimpleDB
				// http://docs.amazonwebservices.com/AmazonSimpleDB/latest/DeveloperGuide/APIError.html
				|| ex.getErrorCode().equals("AccessFailure")
				|| ex.getErrorCode().equals("AuthFailure")
				|| ex.getErrorCode().equals("AuthMissingFailure")
				|| ex.getErrorCode().equals("InternalError")
				|| ex.getErrorCode().equals("RequestExpired")

				// SNS
				// http://docs.amazonwebservices.com/sns/latest/api/CommonErrors.html
				|| ex.getErrorCode().equals("IncompleteSignature")
				|| ex.getErrorCode().equals("InternalFailure")
				|| ex.getErrorCode().equals("InvalidClientTokenId")
				|| ex.getErrorCode().equals("RequestExpired")

				// SQS
				// http://docs.amazonwebservices.com/AWSSimpleQueueService/2011-10-01/APIReference/Query_QueryErrors.html#list-of-errors
				|| ex.getErrorCode().equals("AccessDenied")
				|| ex.getErrorCode().equals("AuthFailure")
				|| ex.getErrorCode().equals(
						"AWS.SimpleQueueService.InternalError")
				|| ex.getErrorCode().equals("InternalError")
				|| ex.getErrorCode().equals("InvalidAccessKeyId")
				|| ex.getErrorCode().equals("InvalidSecurity")
				|| ex.getErrorCode().equals("InvalidSecurityToken")
				|| ex.getErrorCode().equals("MissingClientTokenId")
				|| ex.getErrorCode().equals("MissingCredentials")
				|| ex.getErrorCode().equals("NotAuthorizedToUseVersion")
				|| ex.getErrorCode().equals("RequestExpired")
				|| ex.getErrorCode().equals("X509ParseError")) {

			clearCredentials();

			return true;
		}

		return false;
	}
}

