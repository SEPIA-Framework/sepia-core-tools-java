package net.b07z.sepia.server.core.tools;

import java.util.Collection;
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
	 * Is Java running in Windows?
	 */
	public static boolean systemWindows(){
		return RuntimeInterface.isWindows();
	}

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
	 * Returns "true" if the collection is null or empty.
	 */
	public static boolean nullOrEmpty(Collection<?> l){
		return (l == null || l.isEmpty());
	}
	/**
	 * Returns "true" if the collection is not null and not empty.
	 */
	public static boolean notNullOrEmpty(Collection<?> l){
		return (l != null && !l.isEmpty());
	}
	
	/**
	 * Returns "true" if the string is equal to enum.name().
	 */
	public static boolean typeEqual(String value, Enum<?> type){
		return value.equals(type.name());
	}
	/**
	 * Returns "true" if the string is equal to enum.name() ignoring case.
	 */
	public static boolean typeEqualIgnoreCase(String value, Enum<?> type){
		return value.equalsIgnoreCase(type.name());
	}
}
