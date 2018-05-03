package net.b07z.sepia.server.core.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.tools.JSON;
import spark.Request;

/**
 * This implementation works for GET requests and POST requests that use a form with 'x-www-form-urlencoded' content.
 * 
 * @author Florian Quirin
 *
 */
public class RequestGetOrFormParameters implements RequestParameters {

	Request request;
	
	public RequestGetOrFormParameters(Request request) {
		this.request = request;
	}
	
	@Override
	public String getString(String key) {
		return request.queryParams(key);
	}
	
	@Override
	public String[] getStringArray(String key) {
		return request.queryParamsValues(key);
	}

	@Override
	public JSONObject getJson(String key) {
		String jsonString = request.queryParams(key);
		return JSON.parseStringOrFail(jsonString);
	}
	
	@Override
	public JSONArray getJsonArray(String key) {
		String jsonString = request.queryParams(key);
		return JSON.parseStringToArrayOrFail(jsonString);
	}

	@Override
	public Object getObject(String key) {
		return request.queryParams(key);
	}

	@Override
	public boolean getBoolOrDefault(String key, boolean defaultValue) {
		String boolString = request.queryParams(key);
		if (boolString == null){
			return defaultValue;
		}else{
			return Boolean.valueOf(boolString);
		}
	}
}
