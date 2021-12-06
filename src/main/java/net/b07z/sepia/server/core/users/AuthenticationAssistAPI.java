package net.b07z.sepia.server.core.users;

import java.util.Collection;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.b07z.sepia.server.core.server.BasicStatistics;
import net.b07z.sepia.server.core.server.ConfigDefaults;
import net.b07z.sepia.server.core.tools.Connectors;
import net.b07z.sepia.server.core.tools.Converters;
import net.b07z.sepia.server.core.tools.Debugger;
import net.b07z.sepia.server.core.tools.JSON;

/**
 * Implementation of the {@link AuthenticationInterface} using the assistant-API.
 *  
 * @author Florian Quirin
 *
 */
public class AuthenticationAssistAPI implements AuthenticationInterface{
	
	//Config
	private static final Logger log = LoggerFactory.getLogger(AuthenticationAssistAPI.class);

	//Stuff
	private String userid = "";
	private int errorCode = 0;
	private final HashMap<String, Object> basicInfo = new HashMap<>();
	private int accessLevel = 0;
	
	//authenticate user
	@Override
	public boolean authenticate(JSONObject info) {
		long tic = Debugger.tic();
		
		//check client - client has influence on the password token that is used
		String client = (String) info.get("client");
		if (client == null || client.isEmpty()){
			client = ConfigDefaults.defaultClientInfo;
		}
		
		//Auth. endpoint URL
		String url = ConfigDefaults.defaultAssistAPI + "authentication";
		//System.out.println("Auth. call: " + url); 			//debug
		
		String userid = "anonymous";	//this will stay for tToken because position in tToken object might change
		String dataStr;
		//boolean wasAllowAction = false;
		
		//--- temp. token auth. ---
		if (info.containsKey("tToken")){
			JSONObject tToken = JSON.getJObject(info, "tToken");
			if (tToken.isEmpty()) {
				log.warn("Temp. token empty for user '" + userid);
			}
			//data body
			JSONObject body = JSON.make(
					"tToken", tToken,
					"action", "allow",
					"client", client
			);
			dataStr = body.toJSONString();
			//wasAllowAction = true;
		
		//--- default userid/password auth. ---
		}else{
			userid = (String) info.get("userId");
			String password = (String) info.get("pwd");
			if (password == null || password.trim().isEmpty()) {
				log.warn("Password null or empty for user '" + userid + "': '" + password + "'");
			}
			//data body
			JSONObject body = JSON.make(
					"KEY", (userid + ";" + password),
					"action", "check",
					"client", client
			);
			dataStr = body.toJSONString();
		}
		//System.out.println(dataStr); 			//debug
		
		//headers
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Content-Length", Integer.toString(dataStr.getBytes().length));
		//call server
		JSONObject response = Connectors.httpPOST(url, dataStr, headers);
		//System.out.println(response.toJSONString()); 			//debug
		
		//Status?
		if (!Connectors.httpSuccess(response)){
			log.warn("Authentication ERROR for user '" + userid + "' - original msg.: " + response);
			errorCode = 3; 			//connection error, wrong parameters?
			
			//statistics
			BasicStatistics.addOtherApiHit("AssistAPI.authenticate-error");
			BasicStatistics.addOtherApiTime("AssistAPI.authenticate-error", tic);
			
			return false;
		}
		else{
			String result = (String) response.get("result");
			if (result.equals("fail")){
				if (JSON.getStringOrDefault(response, "error", "").contains("401")){
					log.warn("Authentication failed for user '" + userid + "' - original msg.: " + response);
					errorCode = 2;		//authentication failed
				}else{
					log.warn("Authentication ERROR for user '" + userid + "' - original msg.: " + response);
					errorCode = JSON.getIntegerOrDefault(response, "code", 3);		//authentication ERROR
				}
				//statistics
				BasicStatistics.addOtherApiHit("AssistAPI.authenticate-fail");
				BasicStatistics.addOtherApiTime("AssistAPI.authenticate-fail", tic);
				
				return false;
			}
			//should be fine now - get basic info about user
			copyBasicInfo(response);
			
			//DONE - note: basicInfo CAN be null, so check for it if you use it.
			errorCode = 0; 			//all fine
			
			//statistics
			BasicStatistics.addOtherApiHit("AssistAPI.authenticate");
			BasicStatistics.addOtherApiTime("AssistAPI.authenticate", tic);
			
			return true;
		}
	}
	
	/**
	 * Fill Account data with data received from successful authentication.
	 */
	public void copyBasicInfo(JSONObject response){
		//TODO: add other IDs? (email, phone)
		//NOTE: fields names are defined inside assist-server: net.b07z.sepia.server.assist.users.Authenticator
		
		//GUUID
		this.userid = (String) response.get("uid");
		
		//ACCESS LEVEL 
		//TODO: not yet fully implemented, but should be 0 for access via token, 1 for real password and -1 for no access.
		this.accessLevel = Converters.obj2IntOrDefault(response.get("access_level"), -1);

		//LANGUAGE
		String language = (String) response.get("user_lang_code");
		this.basicInfo.put("user_lang_code", language);
		
		//NAME
		JSONObject user_name = (JSONObject) response.get("user_name");
		this.basicInfo.put("user_name", user_name);
		
		//ROLES
		Object roles_o = response.get("user_roles");
		if (roles_o != null){
			this.basicInfo.put("user_roles", roles_o);
		}
		
		//SHARED ACCESS
		if (response.containsKey("shared_access")){
			this.basicInfo.put("shared_access", response.get("shared_access"));
		}
	}
	
	//get errorCode set during authenticate
	@Override
	public int getErrorCode() {
		return errorCode;
	}
	
	//get basic info acquired during account check
	@Override
	public HashMap<String, Object> getBasicInfo() {
		return basicInfo;
	}
	
	//get user ID
	@Override
	public String getUserID() {
		return userid;
	}

	//get user access level
	@Override
	public int getAccessLevel() {
		return accessLevel;
	}
	
	//------------------------------------------------------------------
	//everything else should not be used inside other APIs than the assist API ... (yet?)

	@Override
	public void setRequestInfo(Object request) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public JSONObject registrationByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createUser(JSONObject info) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteUser(JSONObject info) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean logout(String userid, String client) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean logoutAllClients(String userid) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public JSONArray listUsers(Collection<String> keys, int from, int size){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject requestPasswordChange(JSONObject info) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean changePassword(JSONObject info) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkDatabaseConnection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String userExists(String identifier, String type) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String writeKeyToken(String userid, String client) {
		// TODO Auto-generated method stub
		return null;
	}
}
