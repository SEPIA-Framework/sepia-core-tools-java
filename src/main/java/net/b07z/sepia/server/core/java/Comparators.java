package net.b07z.sepia.server.core.java;

import java.util.Comparator;

import org.json.simple.JSONObject;

/**
 * Class that hold different comparators 
 * 
 * @author Florian Quirin
 *
 */
public class Comparators {
	
	/**
	 * A comparator that takes one or two keys in the JSONObject and compares their long values. Secondary will be used when primary keys are
	 * identical 
	 */
	static public class JsonLongValues implements Comparator<Object>{
		
		String primaryKey = "";
		String secondaryKey = "";
		
		/**
		 * Define one or two keys inside the JSON objects that are long objects and will be used to sort the array.
		 * If you don't need the second key set it as empty.
		 */
		public JsonLongValues(String primaryKey, String secondaryKey){
			this.primaryKey = primaryKey;
			this.secondaryKey = secondaryKey;
		}

		@Override
		public int compare(Object o1, Object o2) {
			JSONObject jo1 = (JSONObject) o1;
			JSONObject jo2 = (JSONObject) o2;
			long lp1 = (long) jo1.get(primaryKey);
			long lp2 = (long) jo2.get(primaryKey);
			int primaryComp = Long.compare(lp1, lp2);
			if (primaryComp == 0 && !secondaryKey.isEmpty()){
				long ls1 = (long) jo1.get(secondaryKey);
				long ls2 = (long) jo2.get(secondaryKey);
				return Long.compare(ls1, ls2);
			}else{
				return primaryComp;
			}
		}
	}
	
	/**
	 * A comparator that takes one or two keys in the JSONObject and compares their string values. Secondary will be used when primary keys are
	 * identical 
	 */
	static public class JsonStringValues implements Comparator<Object>{
		
		String primaryKey = "";
		String secondaryKey = "";
		
		/**
		 * Define one or two keys inside the JSON objects that are string objects and will be used to sort the array.
		 * If you don't need the second key set it as empty.
		 */
		public JsonStringValues(String primaryKey, String secondaryKey){
			this.primaryKey = primaryKey;
			this.secondaryKey = secondaryKey;
		}

		@Override
		public int compare(Object o1, Object o2) {
			JSONObject jo1 = (JSONObject) o1;
			JSONObject jo2 = (JSONObject) o2;
			String lp1 = (String) jo1.get(primaryKey);
			String lp2 = (String) jo2.get(primaryKey);
			int primaryComp = lp1.compareTo(lp2);
			if (primaryComp == 0 && !secondaryKey.isEmpty()){
				String ls1 = (String) jo1.get(secondaryKey);
				String ls2 = (String) jo2.get(secondaryKey);
				return ls1.compareTo(ls2);
			}else{
				return primaryComp;
			}
		}
	}

}
