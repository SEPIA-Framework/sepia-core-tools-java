package net.b07z.sepia.server.core.tools;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

/**
 * Some is-cases that are used super often.
 * 
 * @author Florian Quirin
 *
 */
public class Is {

	/**
	 * Returns "true" if the string is null or empty.
	 */
	public static boolean nullOrEmpty(String s){
		return (s == null || s.isEmpty());
	}
	/**
	 * Returns "true" if the string is not null and not empty.
	 */
	public static boolean notNullOrEmpty(String s){
		return (s != null && !s.isEmpty());
	}
	
	/**
	 * Returns "true" if the JSON object is null or empty.
	 */
	public static boolean nullOrEmpty(JSONObject jo){
		return (jo == null || jo.isEmpty());
	}
	/**
	 * Returns "true" if the JSON object is not null and not empty.
	 */
	public static boolean notNullOrEmpty(JSONObject jo){
		return (jo != null && !jo.isEmpty());
	}
	
	/**
	 * Returns "true" if the map is null or empty.
	 */
	public static boolean nullOrEmptyMap(Map<String, ?> m){
		return (m == null || m.isEmpty());
	}
	/**
	 * Returns "true" if the map is not null and not empty.
	 */
	public static boolean notNullOrEmptyMap(Map<String, ?> m){
		return (m != null && !m.isEmpty());
	}
	
	/**
	 * Returns "true" if the list is null or empty.
	 */
	public static boolean nullOrEmpty(List<?> l){
		return (l == null || l.isEmpty());
	}
	/**
	 * Returns "true" if the list is not null and not empty.
	 */
	public static boolean notNullOrEmpty(List<?> l){
		return (l != null && !l.isEmpty());
	}
}
