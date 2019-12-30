package net.b07z.sepia.server.core.users;

import java.util.HashMap;

import org.json.simple.JSONObject;

/**
 * Interface for any authentication service. Classes that implement the interface must check for user ID, access level and return error codes.
 * Here is the short version of an example chain of events. First step: user sends a registration request with his email address as info, registration()
 * then generates a token consisting of email, time stamp and secret salt (SECRET!), an end point URL and sends it to the user (via email at best).
 * The user clicks the URL that sends him to a page where he can add a password and some basic info (name,..). When he clicks submit, all these elements
 * are sent to the createUser() end point. This end point checks the token, the user ID and if its fine creates the user with all basic entries (prepares
 * the account). From that point on the user can be authenticated via  authenticate(userid, pwd).
 * 
 * @author Florian Quirin
 *
 */
public interface AuthenticationInterface {
	
	/**
	 * Check if the database can be reached and everything is setup properly (e.g. all required indexes are there).
	 * @return
	 */
	public boolean checkDatabaseConnection();
	
	/**
	 * Create the registration info that needs to be send back to the user containing a URL for the endpoint, a time stamp
	 * and a token, all required to call createUser(). This method and createUser() have to share a method for token generation
	 * that can NEVER be published outside of the class!
	 * @param email - unique email for registration
	 * @return JSON object with required info
	 */
	public JSONObject registrationByEmail(String email);
	
	/**
	 * Create a new user with user ID (typically email) and password. The password should be hashed before passed to this method.
	 * The method should check if the user exists before and also create the account with all basic entries (if its not the same source anyway).
	 * @param info - JSON string with info on:<br>
	 * "userid" (typically email address, same as in registration request),<br>
	 * "pwd" (hashed password that gets saved for authentication),<br>
	 * "time" (time stamp of the registration request),<br>
	 * "token" (token received from registration request),<br>
	 * "ticketid" (id of ticket generated for request),<br>
	 * "type" (type of registration, e.g. via email),<br>
	 * "basic_info" (any info the user could additionally submit like name ...).<br>
	 * @return true/false
	 */
	public boolean createUser(JSONObject info);
	
	/**
	 * Similar to create user this method will evaluate info to authorize the delete request.
	 * We might need to add a corresponding method like "registration()" to secure this further, but user name, password should be save.
	 * @param info - JSONObject containing info to delete account. Required keys: "userid".
	 * @return true/false
	 */
	public boolean deleteUser(JSONObject info);
	
	/**
	 * Check if an user exists in the database. Using different IDs like uid, email, phone should be possible.
	 * @param identifier - unique identifier of user (value like email address)
	 * @param type - type of identifier ("uid", "email", "phone", ...)
	 * @return user GUID or empty. Throws exception on DB connection error.
	 */
	public String userExists(String identifier, String type) throws RuntimeException;
	
	/**
	 * Create the info that needs to be send back to the user containing a URL for the endpoint, a time stamp
	 * and a token, all required to call changePassword(). This method and changePassword() have to share a method for token generation
	 * that can NEVER be published outside of the class!
	 * @param info - JSON string with info on:<br>
	 * "userid" (typically email address, same as in registration request),<br>
	 * "type" (type of registration, e.g. via email),<br>
	 * @return json string with required info
	 */
	public JSONObject requestPasswordChange(JSONObject info);
	/**
	 * Gives the user the ability to change his password.
	 * @param info - JSON string with info on:<br>
	 * "userid" (typically email address, same as in registration request),<br>
	 * "time" (time stamp of the registration request),<br>
	 * "token" (token received from registration request),<br>
	 * "ticketid" (ticket id pointing to temporary token),<br>
	 * "type" (type of registration, e.g. via email),<br>
	 * "new_pwd" (new user password, hashed in client?).<br>
	 * @return true/false
	 */
	public boolean changePassword(JSONObject info);
	
	/**
	 * Any authentication request coming from the network might include additional info that is needed besides user name and password.
	 * With this method you can submit any object that might contain this info and use it later during authentication.
	 * @param request - any object that contains the required info like a server request
	 */
	public void setRequestInfo(Object request);
	
	/**
	 * Classical user name, password combination. This might either check the values directly or communicate with an identity platform.
	 * During the process it is supposed to set the values for user ID, access level and error codes during any additional communication.
	 * @param info - JSONObject with:<br>
	 * userId - user name or empty string if only request info (header from setRequest()) is used<br>
	 * pwd - password<br>
	 * idType - type of ID, e.g. "email"<br>
	 * client - client info for token path<br>
	 * @return true/false
	 */
	public boolean authenticate(JSONObject info);
	
	/**
	 * Create a key token, write it to database and return it. Should be used to do save logins without real password. 
	 * @param userid - ID
	 * @param client - depending on the client different tokens can be used  
	 * @return created token, to send it to user or empty string if writing failed
	 */
	public String writeKeyToken(String userid, String client); 
	
	/**
	 * Typically a website will use the token issued during the last log-in procedure to authenticate the user. Logout() should 
	 * make this token invalid thus making further use of the token impossible on any machine.  
	 * @param userid - id to log out
	 * @param client - depending on the client different tokens can be used  
	 * @return
	 */
	public boolean logout(String userid, String client);
	
	/**
	 * Typically a website or app will use a token issued during the last log-in procedure to authenticate the user. logoutAllClients() should 
	 * make all tokens invalid thus making further use of the token impossible on any machine.  
	 * @param userid - id to log out
	 * @return
	 */
	public boolean logoutAllClients(String userid);
	
	/**
	 * After a successful authentication the class obtains the user ID associated with the user. It is the unique identifier to access any database info etc.
	 * @return unique user ID (-1 if the user was not authenticated)
	 */
	public String getUserID();
	
	/**
	 * After a successful authentication the class obtains the user access level. Access levels control information flow and might be increased by
	 * a multi-factor authentication (biometry, location, device info etc.)
	 * @return authentication level starting at -1 (no access) and 0 (lowest, with very limited access to personal info)
	 */
	public int getAccessLevel();
	
	/**
	 * During authentication some basic info of the user, like user name etc. can be obtained and passed down to the token/user.
	 * Info is stored in this HashMap.
	 * @return
	 */
	public HashMap<String, Object> getBasicInfo();
	
	/**
	 * Returns an error code set during authentication to give you more info about what went wrong.
	 * Use:<br>
	 * 0 - no errors <br>
	 * 1 - communication error (like server did not respond) <br>
	 * 2 - access denied (due to wrong credentials or whatever reason) <br>
	 * 3 - might be 1 or 2 (where 2 can also be due to wrong parameters)<br>
	 * 4 - unknown error <br>
	 * 5 - during registration/requestPasswordChange: user existence check failed; during createUser/changePassword: invalid token or time stamp<br>
	 * 6 - password format invalid<br>
	 * 7 - UID generation or storing failed<br>
	 * 
	 * @return integer error code
	 */
	public int getErrorCode();

}
