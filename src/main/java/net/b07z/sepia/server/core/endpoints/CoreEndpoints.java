package net.b07z.sepia.server.core.endpoints;

import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.server.BasicStatistics;
import net.b07z.sepia.server.core.server.RequestGetOrFormParameters;
import net.b07z.sepia.server.core.server.RequestParameters;
import net.b07z.sepia.server.core.server.SparkJavaFw;
import net.b07z.sepia.server.core.server.Validate;
import net.b07z.sepia.server.core.tools.JSON;
import spark.Request;
import spark.Response;

/**
 * A couple of endpoints that are useful for every server.
 *   
 * @author Florian Quirin
 *
 */
public class CoreEndpoints {

	/**
	 * ---ONLINE CHECK---<br>
	 * End-point to check if the server can be reached. Returns empty answer.<br>
	 * Note: this is a GET endpoint
	 */
	public static String onlineCheck(Request request, Response response){
		//stats
		BasicStatistics.addOtherApiHit("Online endpoint");
		BasicStatistics.addOtherApiTime("Online endpoint", 1);
		
		return SparkJavaFw.returnResult(request, response, "", 204);
	}

	/**
	 * ---PING SERVER---<br>
	 * Ping server to see if its online.<br>
	 * Note: this is a GET endpoint
	 */
	public static String ping(Request request, Response response, String serverName){
		//stats
		BasicStatistics.addOtherApiHit("Ping endpoint");
		BasicStatistics.addOtherApiTime("Ping endpoint", 1);
		
		JSONObject msgJSON = JSON.make("result", "success", "server", serverName);
		return SparkJavaFw.returnResult(request, response, msgJSON.toJSONString(), 200);
	}

	/**
	 * ---VALIDATE SERVER---<br>
	 * End-point to ping the server and get back some basic info like API version 
	 * and signature (calculated from local_name, local_secret and challenge).<br>
	 * Note: this is a GET endpoint
	 */
	public static String validateServer(Request request, Response response, 
			String serverName, String apiVersion, String localName, String localSecret){
		//stats
		BasicStatistics.addOtherApiHit("Validation endpoint");
		BasicStatistics.addOtherApiTime("Validation endpoint", 1);
		
		//prepare parameters
		RequestParameters params = new RequestGetOrFormParameters(request);
					
		long t = System.currentTimeMillis();
		JSONObject msg = new JSONObject();
		JSON.add(msg, "result", "success");
		JSON.add(msg, "server", serverName);
		JSON.add(msg, "version", apiVersion);
		JSON.add(msg, "host", request.host());
		JSON.add(msg, "url", request.url());
		JSON.add(msg, "time", t);
		JSON.add(msg, "signature", Validate.getLocalSignature(request, params.getString("challenge"), t, localName, localSecret));
	
		return SparkJavaFw.returnResult(request, response, msg.toJSONString(), 200);
	}
	
	

}
