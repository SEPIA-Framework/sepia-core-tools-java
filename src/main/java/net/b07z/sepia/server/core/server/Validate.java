package net.b07z.sepia.server.core.server;

import net.b07z.sepia.server.core.tools.Debugger;
import net.b07z.sepia.server.core.tools.Security;
import spark.Request;

/**
 * Class to validate the server to user. Especially important for local servers when sending a password/token to the server.
 * 
 * @author Florian Quirin
 *
 */
public class Validate {
		
	/**
	 * Get local server signature to verify if its trustful.
	 * @param request - info that can be used to check for example host or IP
	 * @param challenge - "challenge" the server with a custom string
	 * @param t - time
	 * @param name - server name
	 * @param secret - secret shared between server and client
	 * @return signature string
	 */
	public static String getLocalSignature(Request request, String challenge, long t, String name, String secret){
		if (challenge == null) {
			challenge = "";
		}
		try {
			return Security.bytearrayToHexString(Security.getSha256(name + t + challenge + secret));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Check if this was a valid intra-API call from e.g. assistant-API
	 * @param request - input request to check for e.g. IP
	 * @param submittedKey - key submitted via request parameters
	 * @param sharedServerKey - secret key shared between servers of same network
	 * @return
	 */
	public static boolean validateInternalCall(Request request, String submittedKey, String sharedServerKey) {
		//TODO: make this more secure by adding a white-list of IPs or something ...
		if (submittedKey != null && submittedKey.equals(sharedServerKey)){
			return true;
		}else if (submittedKey != null){
			Debugger.println("Invalid internal API call from " + request.ip(), 1);
			return false;
		}else{
			return false;
		}
	}

}
