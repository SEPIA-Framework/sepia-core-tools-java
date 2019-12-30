package net.b07z.sepia.server.core.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.b07z.sepia.server.core.server.ConfigDefaults;
import net.b07z.sepia.server.core.server.RequestParameters;
import net.b07z.sepia.server.core.tools.ClassBuilder;
import net.b07z.sepia.server.core.tools.Converters;
import net.b07z.sepia.server.core.tools.Is;
import net.b07z.sepia.server.core.tools.JSON;
import net.b07z.sepia.server.core.tools.Security;

/**
 * This class holds info about the user account acquired during authentication.
 * It is the light version of the "User" class in assistant-API so I gave it a new name to avoid possible future conflicts.
 * Keep it slim and light!
 * 
 * @author Florian Quirin
 */
public class Account {

	//private static final Logger log = LogManager.getLogger();
	private static final Logger log = LoggerFactory.getLogger(Account.class);

	//service that is able to perform user authentication
	private final AuthenticationInterface authService = (AuthenticationInterface) ClassBuilder.construct(ConfigDefaults.defaultAuthModule);
	
	//account basic user data
	private String userId = "";				//unique ID of user
	private String email = "";				//other IDs (number, email, whatever ...), acquired during authentication, must not be changed afterwards!
	private String phone = "";				//...
	private int accessLevel = -1;				//level of access depending on user account authentication (0 is the lowest)
	private JSONObject userName;				//object with nick, first and last field;
	private String userNameShort = "";			//short version of name, ready to use :-)
	private String language = "en";				//user account language (ISO-639 code)
	private String userBirth = "";				//user birth date
	private List<String> userRoles;			//user roles managing certain access rights
	//more
	private Map<String, Object> info = new HashMap<>(); 	//info for everything that's not in the basics
	
	/**
	 * Sometimes you need a temporary token that is bound to certain account properties. 
	 * @param userId
	 * @param userRoles
	 * @param secret1
	 * @param secret2
	 * @param iterations
	 * @return
	 * @throws Exception
	 */
	public static String getTemporaryValidationToken(String userId, List<String> userRoles, String secret1, String secret2, int iterations) 
			throws Exception{
		if (Is.nullOrEmpty(userId) || userRoles == null || Is.nullOrEmpty(secret1) || Is.nullOrEmpty(secret2) || iterations == 0){
			throw new RuntimeException("Invalid input for 'getTemporaryValidationToken'");
		}
		//build token
		return Security.bytearrayToHexString(Security.getEncryptedPassword(
				secret1.replaceAll("\\s", "").trim() + secret2.replaceAll("\\s", "").trim(), 
				Security.getSha256((userId + userRoles.toString()).replaceAll("\\s", "").trim()), 
				iterations, 32
		));
	}
	
	/**
	 * Return unique user ID.
	 */
	public String getUserID(){
		return userId;
	}
	/**
	 * Return email user ID
	 */
	public String getEmail(){
		return email;
	}
	/**
	 * Return phone user ID.
	 */
	public String getPhone(){
		return phone;
	}

	/**
	 * Name data string, e.g. "<nickN>Jim<firstN>James<lastN>Last";
	 */
	public JSONObject getUserName() {
		return userName;
	}
	
	/**
	 * Simple name to address the user in a UI or something.
	 */
	public String getUserNameShort() {
		return userNameShort;
	}
	
	/**
	 * Get user language.
	 */
	public String getPreferredLanguage(){
		return language;
	}
	
	/**
	 * Get user birth date
	 */
	public String getBirthDate(){
		return userBirth;
	}
	
	/**
	 * Return user access level.
	 */
	public int getAccessLevel(){
		return accessLevel;
	}

	/**
	 * User's access permissions.
	 */
	public List<String> getUserRoles() {
		return userRoles;
	}

	public boolean hasRole(String roleName){
		return userRoles != null && userRoles.contains(roleName);
	}
	
	/**
	 * Get a short version of the user's name like his nick name (if defined).
	 * @return nick name, first name, last name or default name (Boss) 
	 */
	private String getShortUserName(){
		String name = "Boss";
		if (userName == null || userName.isEmpty()){
			return name;
		}
		String nick = (String) userName.get("nick");
		String first = (String) userName.get("first");
		String last = (String) userName.get("last");
		
		//check if any of the data is available in the order nick, first, last
		if (nick!=null && !nick.isEmpty()){
			name = nick;
		}else if (first!=null && !first.isEmpty()){
			name = first;
		}else if (last!=null && !last.isEmpty()){
			name = last;
		}
		return name;
	}
	
	/**
	 * Authenticate the user. Copies basic user info to this class on successful authentication. 
	 * @param requestJson - the request (aka URL parameters) sent to server as JSON object (parsed request body)
	 * @return true or false
	 */
	/*
	public boolean authenticate(JSONObject requestJson){
		String[] params = new String[4];
		params[0] = "KEY=" + requestJson.get("KEY");
		params[1] = "GUUID=" + requestJson.get("GUUID");
		params[2] = "PWD=" + requestJson.get("PWD");
		params[3] = "client=" + requestJson.get("client");
		FakeRequest authRequest = new FakeRequest(params);
		return authenticate(new RequestGetOrFormParameters(authRequest));
	}
	*/
	/**
	 * Authenticate the user. Copies basic user info to this class on successful authentication. 
	 * @param params - {@link RequestParameters}
	 * @return true or false
	 */
	public boolean authenticate(RequestParameters params){
		//client data?
		String clientInfo = params.getString("client");
		if (clientInfo == null || clientInfo.isEmpty()){
			clientInfo = ConfigDefaults.defaultClientInfo;
		}
		
		//--- Temp. Token ---
		
		//check for temporary token first
		JSONObject tToken = params.getJson("tToken");
		if (tToken != null){
			//call
			JSONObject authInfo = JSON.make(
					"tToken", tToken,
					"client", clientInfo
			);
			if (authService.authenticate(authInfo)){
				//get basic info
				copyBasicInfo();
				return true;
			}else{
				//fail
				return false;
			}
		}
		
		//--- Default ---
		
		//get username/password parameters
		String key = params.getString("KEY");
		if (key == null || key.isEmpty()){
			String guuid = params.getString("GUUID");
			String pwd = params.getString("PWD");
			if (guuid != null && pwd != null && !guuid.isEmpty() && !pwd.isEmpty()){
				key = guuid + ";" + Security.hashClientPassword(pwd);
			} 
		}
		//check
		if (key != null && !key.isEmpty()){
			String[] up = key.split(";",2);
			if (up.length == 2){
				String username = up[0].toLowerCase();
				String password = up[1];
				//call
				JSONObject authInfo = JSON.make(
						"userId", username,
						"pwd", password,
						"client", clientInfo
				);
				if (authService.authenticate(authInfo)){
					//get basic info
					copyBasicInfo();
					return true;
				}else{
					//fail
					return false;
				}
			//ERROR
			}else{
				log.error("AUTHENTICATION FAILED! Due to wrong KEY format: '" + key + "'");
				return false;
			}
		//ERROR
		}else{
			log.error("AUTHENTICATION FAILED! Due to missing KEY");
			return false;
		}
	}
	
	/**
	 * Fill Account data with data received from successful authentication.
	 */
	public void copyBasicInfo(){
		userId = authService.getUserID();
		accessLevel = authService.getAccessLevel();
		
		info = authService.getBasicInfo();
		
		//Language
		String lang_code = (String) info.get("user_lang_code");
		if (lang_code != null && !lang_code.isEmpty()){
			language = lang_code;
		}
		
		//Name
		JSONObject user_n = (JSONObject) info.get("user_name");
		if (user_n != null && !user_n.isEmpty()){
			userName = user_n;
			userNameShort = getShortUserName();
		}
		
		//Birth
		String user_birth = (String) info.get("user_birth");
		if (user_birth != null && !user_birth.isEmpty()){
			userBirth = user_birth;
		}
		
		//Roles
		if (info.containsKey("user_roles")){
			userRoles = Converters.object2ArrayListStr(info.get("user_roles"));
		}else{
			userRoles = new ArrayList<>();
		}
	}
	
	/**
	 * Export (part of) user account data to JSON string.
	 */
	public JSONObject exportJSON(){
		JSONObject account = new JSONObject();
		JSON.add(account, "userId", userId);
		JSON.add(account, "email", email);
		JSON.add(account, "phone", phone);
		JSON.add(account, "userName", userName.toJSONString());
		JSON.add(account, "accessLevel", accessLevel);
		if (userRoles != null && !userRoles.isEmpty()){
			JSON.add(account, "userRoles", JSON.stringListToJSONArray(userRoles));
		}
		JSON.add(account, "prefLanguage", language);
		JSON.add(account, "userBirth", userBirth);
		return account;
	}
	/**
	 * Import account from JSONObject. Make sure user was empty before!
	 */
	public void importJSON(JSONObject account){
		//reset just to make sure
		info = new HashMap<String, Object>();
		//ID, LVL, NAME
		userId = (String) account.get("userId");
		email = (String) account.get("email");
		phone = (String) account.get("phone");
		accessLevel = Converters.obj2IntOrDefault(account.get("accessLevel"), -1);
		JSONObject uname = (JSONObject) account.get("userName");
		if (uname != null){
			userName = uname;
		}
		//pref. LANG
		String pl = (String) account.get("prefLanguage");
		if (pl != null && !pl.isEmpty()){
			language = pl;
		}
		//BIRTH
		String ub = (String) account.get("userBirth");
		if (ub != null && !ub.isEmpty()){
			userBirth = ub;
		}
		//ROLES
		JSONArray ja = (JSONArray) account.get("userRoles");
		List<String> allRoles = new ArrayList<>();
		if (ja != null && !ja.isEmpty()){
			for (Object o : ja){
				allRoles.add(((String) o).toLowerCase());
			}
			userRoles = allRoles;
		}
	}

}
