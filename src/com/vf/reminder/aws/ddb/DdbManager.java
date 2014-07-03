package com.vf.reminder.aws.ddb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vf.reminder.MainActivity;
import com.vf.reminder.aws.sns.SNSMobilePush;
import com.vf.reminder.db.tables.Friends;
import com.vf.reminder.db.tables.Reminder;
import com.vf.reminder.db.tables.User;
import com.vf.reminder.utils.Utils;

public class DdbManager {

	/*
	 * 5 Phones No's with 12 Digits is 64Bytes
	 * 64KB - 1 * 1000
	 * 64KB - MAX 5000 Phones No's (excluding Remaining Data, So can easily support 1000 Phone No's)
	 * 
	 *  Saving Phones No's in User table.
	 * 
	 */
	
	private static final String TAG = "DdbManager";
	
	AmazonDynamoDBClient ddbClient; 
	DynamoDBMapper mapper;
	DBResponseHelper<User> response = new DBResponseHelper<User>();
	
	public DdbManager(){
		ddbClient = MainActivity.clientManager.ddb();
		 mapper = new DynamoDBMapper(ddbClient);
	}
	
	public DBResponseHelper<User> newUser(User u, List<String> lstContacts){
		
		try{
			Utils.debug(TAG, u.toString());
			Utils.debug(TAG, "total no contacts"+lstContacts.size());
			List<Friends> lstFriends  = refreshFriends(lstContacts);
			Utils.debug(TAG, "total no friends found"+lstFriends.size());
			new Gson().toJson(lstFriends,new TypeToken<List<Friends>>(){}.getType());
			
		
			// Create an Endpoint. This corresponds to an app on a device.
			SNSMobilePush sns = new SNSMobilePush(MainActivity.clientManager.sns());
			String endPointArn = "";
			try{
				
				 CreatePlatformEndpointResult platformEndpointResult = sns.createPlatformEndpoint(
			                "For mobile "+u.getMobile(), u.getRegId(), SNSMobilePush.PLATFORM_APPLICATION_ARN);
			        System.out.println(platformEndpointResult);
				 endPointArn = platformEndpointResult.getEndpointArn();
			}
			
			 
			catch(Exception ex){
				  
				ex.printStackTrace();
				String errMsg = ex.getMessage();
				Utils.debug(TAG, errMsg);
				
				//ignore the error
			}
			
	     
	        
	        
			Utils.debug(TAG, endPointArn);
	        u.setLstFriends(lstFriends);
	        u.setArn(endPointArn);
	        u.setTime(System.currentTimeMillis());
	        Utils.debug(TAG, "saving into DB");
	        
			
			mapper.save(u);
			response.setSuccess(true);
			response.setT(u);
		}
		catch(Exception ex){
			ex.printStackTrace();
			response.setErrMessage("Failed to Initialize, Please try later");
			response.setSuccess(false);
		}
		return response;
	}
	
private void loadUserApi(String hashKey){
	 Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
	 
     key.put("mobile", new AttributeValue().withS(hashKey));
     
     GetItemRequest getItemRequest = new GetItemRequest()
         .withTableName("User")
         .withKey(key)
         .withAttributesToGet(Arrays.asList("mobile", "time", "status"));
     
     GetItemResult result = ddbClient.getItem(getItemRequest);

     // Check the response.
     System.out.println("Printing item after retrieving it....");
     printItem(result.getItem());            
}
	
private static void printItem(Map<String, AttributeValue> attributeList) {
    for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
        String attributeName = item.getKey();
        AttributeValue value = item.getValue();
        System.out.println(attributeName + " "
                + (value.getS() == null ? "" : "S=[" + value.getS() + "]")
                + (value.getN() == null ? "" : "N=[" + value.getN() + "]")
                + (value.getB() == null ? "" : "B=[" + value.getB() + "]")
                + (value.getSS() == null ? "" : "SS=[" + value.getSS() + "]")
                + (value.getNS() == null ? "" : "NS=[" + value.getNS() + "]")
                + (value.getBS() == null ? "" : "BS=[" + value.getBS() + "] \n"));
    }
}

	public List<Friends> refreshFriends(List<String> lstContacts){
		List<Friends> lstFriends = new ArrayList<Friends>();
		for(String contact : lstContacts){
			Utils.debug(TAG, "Verifying for contact ... "+contact);
			User u =  mapper.load(User.class, contact);
			
			Utils.debug(TAG, "Verifying for contact .found??. "+u);
			if(u==null){
				continue;
			}
			Friends f = new Friends();
			Utils.debug(TAG, "Adding new user "+u.getMobile());
			f.setIdMobile(contact);
			f.setStat("ACTIVE");
			lstFriends.add(f);
		}
		
		return lstFriends;
	
	}
	
	
	public List<Friends> refreshFriends(List<String> lstContacts, User u ){
		HashSet<Friends> lstNewFriends = new HashSet<Friends>();
		for(String contact : lstContacts){
			Friends f = new Friends();
			f.setIdMobile(contact);
			f.setStat("New");
			lstNewFriends.add(f);
			
		}
		
		List<Friends> lstOldFriends = u.getLstFriends();
		lstNewFriends.removeAll(lstOldFriends);
		for(Friends newContact : lstNewFriends){
			
			User uNew =  mapper.load(User.class, newContact.getIdMobile());
			Utils.debug(TAG, "checking for contact ... "+newContact + " retrieved? "+uNew );
			if(uNew==null){
				continue;
			}
			Friends f = new Friends();
			f.setIdMobile(newContact.getIdMobile());
			f.setStat("ACTIVE");
			Utils.debug(TAG, "New friend added : "+newContact.getIdMobile());
			lstOldFriends.add(f);
		}
		
		return lstOldFriends;
	
	}
	
	public User getUser(String mobile){
		return mapper.load(User.class, mobile);
	}
	
	
	//friends reminder
	public String saveFriendReminder(Reminder r){
		mapper.save(r);
		Utils.debug(TAG, "Remote Id generated is : "+r.getRemoteId());
		return r.getRemoteId();
		
	}
}
