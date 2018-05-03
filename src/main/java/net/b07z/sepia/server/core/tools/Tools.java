package net.b07z.sepia.server.core.tools;

import java.util.Map;
import java.util.Optional;

public final class Tools {
	
	private Tools() {
	}

	/**
	 * Check if a 'test' value is part of an enum. 
	 * @param enumValues - enum.values
	 * @param test - test string
	 * @return true/false
	 */
	public static <T extends Enum<T>> boolean enumContains(T[] enumValues, String test){
	    for (T ev : enumValues) {
	        if (ev.name().equals(test)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * Get the key of a map whose value matches a given test-value. Note: if the value itself is a complex object you need to rewrite this method. 
	 * @param val - test value
	 * @param map - map to search
	 * @return key or null
	 */
	public static <K, V> Optional<K> getKeyForVal(final V val, final Map<K, V> map) {
		try{
			return map.entrySet().stream()
					.filter(e -> e.getValue().equals(val))
			        .map(Map.Entry::getKey)
			        .findFirst();
		}catch(Exception e){
			return null;
		}
	}

}
