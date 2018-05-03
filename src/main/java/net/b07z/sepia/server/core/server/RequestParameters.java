package net.b07z.sepia.server.core.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Abstraction layer for SparkJava Request to unify parameter read-out for GET and POST methods. 
 * 
 * @author Florian Quirin
 *
 */
public interface RequestParameters {
	
	/**
	 * Get input data at 'key' as string
	 * @param key
	 */
	public String getString(String key);
	
	/**
	 * Get input data at 'key' as array of strings
	 * @param key
	 */
	public String[] getStringArray(String key);
	
	/**
	 * Get input data at 'key' as JSON.
	 * @param key
	 * @return
	 */
	public JSONObject getJson(String key);
	
	/**
	 * Get input data at 'key' as JSONArray.
	 * @param key
	 * @return
	 */
	public JSONArray getJsonArray(String key);
	
	/**
	 * Get input data at 'key' as Object.
	 * @param key
	 * @return
	 */
	public Object getObject(String key);
	
	/**
	 * Get boolean at 'key'. If the parameter does not exists return default.
	 * @param key
	 * @return boolean value at key or default
	 */
	public boolean getBoolOrDefault(String key, boolean defaultValue);

}
