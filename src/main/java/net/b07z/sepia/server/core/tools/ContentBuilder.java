package net.b07z.sepia.server.core.tools;

import java.net.URLEncoder;

import org.json.simple.JSONObject;

/**
 * Build objects and strings for different types of requests, e.g. HTTP form POST.
 * 
 * @author Florian Quirin
 *
 */
public class ContentBuilder {
	
	/**
	 * Build a string that can be used for HTTP POST with x-www-form-urlencoded content. 
	 * Make sure that you submit an even number of parameters or the method will throw an exception.
	 * @param keyValues - tuples of key-values. Values will be cast to string.
	 * @return String like "a=1&b=2" with url-encoding
	 */
	public static String postForm(Object... keyValues){
		try {
			String parameters = keyValues[0] + "=" + keyValues[1];
			int i = 2;
			while (i < keyValues.length){
				parameters += "&" + (keyValues[i] + "=" + URLEncoder.encode(keyValues[i+1].toString(), "UTF-8"));
				i = i + 2;
			}
			return parameters;
			
		} catch (Exception e) {
			throw new RuntimeException(DateTime.getLogDate() + " WARNING - ContentBuilder.java / postForm() - Failed to build: " + keyValues, e);
		}
	}
	
	/**
	 * Build an JSON object that can be used for HTTP POST with JSON request body. 
	 * Make sure that you submit an even number of parameters or the method will throw an exception.
	 * @param keyValues - tuples of key-values.
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject postJson(Object... keyValues){
		try {
			JSONObject jo = new JSONObject();
			int i = 0;
			while (i < keyValues.length){
				jo.put(keyValues[i], keyValues[i+1]);
				i = i + 2;
			}
			return jo;
			
		} catch (Exception e) {
			throw new RuntimeException(DateTime.getLogDate() + " WARNING - ContentBuilder.java / postJson() - Failed to build: " + keyValues, e);
		}
	}

}
