package net.b07z.sepia.server.core.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.tools.JSON;
import spark.Request;

/**
 * This implementation works for classic POST requests ('application/json' content').
 * For form-submitted content please use {@link RequestGetOrFormParameters}.
 * 
 * @author Florian Quirin
 * 
 */
public class RequestPostParameters implements RequestParameters {

	private JSONObject requestBody;
	
	/**
	 * Default constructor.
	 * @param request
	 */
	public RequestPostParameters(Request request) {
		requestBody = SparkJavaFw.getRequestBody(request);
	}
	/**
	 * Custom constructor.
	 * @param json
	 */
	public RequestPostParameters(JSONObject json) {
		requestBody = json;
	}
	
	@Override
	public String getString(String key) {
		return (String) requestBody.get(key);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String[] getStringArray(String key) {
		JSONArray jArray = getJsonArray(key);
		if (jArray != null){
			return (String[]) jArray.toArray(new String[]{}); 		//TODO: untested
		}else{
			return null;
		}
	}

	@Override
	public JSONObject getJson(String key) {
		return (JSONObject) requestBody.get(key);
	}
	
	@Override
	public JSONArray getJsonArray(String key) {
		return JSON.getJArray(requestBody, key);
	}

	@Override
	public Object getObject(String key) {
		return requestBody.get(key);
	}
	
	@Override
	public boolean getBoolOrDefault(String key, boolean defaultValue) {
		Object boolObject = requestBody.get(key);
		if (boolObject == null){
			return defaultValue;
		}else{
			return (Boolean) boolObject;
		}
	}
}
