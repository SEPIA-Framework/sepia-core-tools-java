package net.b07z.sepia.server.core.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * This class is supposed to make life easier handling JSON data ^^.
 * 
 * @author Florian Quirin
 *
 */
public final class JSON {
	
	private JSON() {
	}
	
	/**
	 * Make a JSONObject by simply giving "key" and "value".
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject make(String key, Object value){
		JSONObject jo = new JSONObject();
		jo.put(key, value);
		return jo;
	}
	/**
	 * Make a JSONObject by simply giving 2 keys and values.
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject make(String key, Object value, String key2, Object value2){
		JSONObject jo = new JSONObject();
		jo.put(key, value);
		jo.put(key2, value2);
		return jo;
	}
	/**
	 * Make a JSONObject by simply giving 3 keys and values.
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject make(String key, Object value, String key2, Object value2, 
			String key3, Object value3){
		JSONObject jo = make(key, value, key2, value2);
		jo.put(key3, value3);
		return jo;
	}
	/**
	 * Make a JSONObject by simply giving 4 keys and values.
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject make(String key, Object value, String key2, Object value2, 
			String key3, Object value3, String key4, Object value4){
		JSONObject jo = make(key, value, key2, value2, key3, value3);
		jo.put(key4, value4);
		return jo;
	}
	/**
	 * Make a JSONObject by simply giving 5 keys and values.
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject make(String key, Object value, String key2, Object value2, 
			String key3, Object value3, String key4, Object value4, String key5, Object value5){
		JSONObject jo = make(key, value, key2, value2, key3, value3, key4, value4);
		jo.put(key5, value5);
		return jo;
	}
	/**
	 * Make a JSONObject by simply giving 6 keys and values.
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject make(String key, Object value, String key2, Object value2, 
			String key3, Object value3, String key4, Object value4, String key5, Object value5, 
			String key6, Object value6){
		JSONObject jo = make(key, value, key2, value2, key3, value3, key4, value4, key5, value5);
		jo.put(key6, value6);
		return jo;
	}
	
	/**
	 * Make a JSONArray by adding Objects.
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray makeArray(Object... objects){
		JSONArray ja = new JSONArray();
		for (Object o : objects){
			ja.add(o);
		}
		return ja;
	}
	
	/**
	 * Parse a string to a JSONObject.
	 * @param response - string response from any source
	 * @return JSON object of string or null
	 */
	public static JSONObject parseString(String response){
		try {
			return parseStringOrFail(response);

		} catch (Exception e) {
			System.err.println(DateTime.getLogDate() + " ERROR - JSON.java / parseString() - Failed to parse JSON string: " + response);
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Parse a string to a JSONObject or fail.
	 * @param response - string response from any source
	 * @return JSON object of string or throw RuntimeException
	 */
	public static JSONObject parseStringOrFail(String response){
		try {
			JSONParser parser = new JSONParser();
			JSONObject result = (JSONObject) parser.parse(response);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(DateTime.getLogDate() + " WARNING - JSON.java / parseString() - Failed to parse JSON string: " + response, e);
		}
	}
	
	/**
	 * Parse a string to a JSONArray or fail by throwing RuntimeException.
	 */
	public static JSONArray parseStringToArrayOrFail(String response){
		try {
			JSONParser parser = new JSONParser();
			JSONArray result = (JSONArray) parser.parse(response);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(DateTime.getLogDate() + " WARNING - JSON.java / parseStringToArray() - Failed to parse JSON string: " + response, e);
		}
	}
	
	/**
	 * Print content of JSONObject to system.out. Keys and values must be convertible to string!<br>
	 * For a more elegant way consider: {@link #prettyPrint(JSONObject)}.
	 * @param input - JSONObject to print
	 */
	@SuppressWarnings("unchecked")
	public static void printJSON(JSONObject input){
		input.forEach( (key, value) -> {
			System.out.println("Key: " + key + " --- " + "Value: " + value );
		});
	}
	
	/**
	 * Print content of JSONObject in a kind of prettier way to stdOut. Keys and values must be convertible to string!<br>
	 * Used only for quick debugging, if you need more options use {@link JSONWriter} or simply {@link #prettyPrint(JSONObject)}.
	 */
	public static void printJSONpretty(JSONObject input){
		printJSONpretty(input, "-");
	}
	/**
	 * Print content of JSONObject in a kind of prettier way. Keys and values must be convertible to string!<br>
	 * Used only for quick debugging, if you need more options use {@link JSONWriter} or simply {@link #prettyPrint(JSONObject)}.
	 */
	@SuppressWarnings("unchecked")
	public static void printJSONpretty(JSONObject input, String indentSymbol){
		input.forEach( (key, value) -> {
			if (value != null && value.getClass().equals(JSONObject.class)){
				System.out.println(indentSymbol + "[START object] " + key + ": " + value);
				printJSONpretty((JSONObject) value, indentSymbol + indentSymbol);
				System.out.println(indentSymbol + "[END object] " + key);
			}else{
				System.out.println(indentSymbol + key + ": " + value );
			}
		});
	}
	/**
	 * Properly print JSONObject in a pretty form.
	 * @param json - JSON object
	 */
	public static void prettyPrint(JSONObject json){
		System.out.println(JSONWriter.getPrettyString(json));
	}
	
	/**
	 * Get keys of JSON object as set of strings.
	 * @param json - JSONObject
	 */
	@SuppressWarnings("unchecked")
	public static Set<String> getKeys(JSONObject json){
		Set<String> keys = json.keySet();
		return keys;
	}
	
	/**
	 * Get true/false boolean of a JSONObject named "key". The object can be a string or a boolean.
	 * 
	 * @param j_obj - JSONObject input
	 * @param key - key name of the entry you want, ...get("key")
	 * @return value as boolean, fail is false!
	 */
	public static boolean getBoolean(JSONObject j_obj, String key){
		if (j_obj != null && j_obj.containsKey(key)){
			boolean value;
			value = Boolean.valueOf(j_obj.get(key).toString());
			return value;
		}else{
			return false;
		}
	}
	
	/**
	 * Get integer value of key-field or default value.
	 */
	public static int getIntegerOrDefault(JSONObject jObj, String key, int defaultValue){
		try{
			if (jObj != null && jObj.containsKey(key)){
				return Integer.valueOf(jObj.get(key).toString());
			}
		}catch(Exception e){}
		return defaultValue;
	}
	
	/**
	 * Get long value of key-field or default value.
	 */
	public static long getLongOrDefault(JSONObject jObj, String key, long defaultValue){
		try{
			if (jObj != null && jObj.containsKey(key)){
				return Long.valueOf(jObj.get(key).toString());
			}
		}catch(Exception e){}
		return defaultValue;
	}
	
	/**
	 * Get a string "value" of a JSONObject named "key".
	 * Long: If you have an JSONObject and you want the value of a key name as a String
	 * than you can get it with this method ;-)
	 * 
	 * @param j_obj - JSONObject input
	 * @param key - key name of the entry you want, ...get("key")
	 * @return value as string or empty string (or null if value is actually null)
	 */
	public static String getString(JSONObject j_obj, String key){
		if (j_obj != null && j_obj.containsKey(key)){
			Object value = j_obj.get(key);
			if (value == null){
				return null;
			}else{
				return value.toString();
			}
		}else{
			return "";
		}
	}
	
	/**
	 * Get a string "value" of a JSONObject named "key" from JSONArray "index" entry. 
	 * Note: the JSONArray must contain JSONObjects. 
	 * Long: If you have an JSONArray and you want the JSONObject at a certain index of the array and that object is an array
	 * than you can get it with this method ;-). Checks for null, array size and JSONObject contains ...
	 * 
	 * @param j_array - JSONArray input
	 * @param index - array position in JSONArray 
	 * @param key - key name of the entry you want, ...get("key")
	 * @return value as String or empty string
	 */
	public static String getString(JSONArray j_array, int index, String key){
		if (j_array != null && ((j_array.size()-1) >= index)){
			String value;
			JSONObject jso = (JSONObject) j_array.get(index);
			if (jso != null && jso.containsKey(key)){
				value = (String) ((JSONObject) (j_array.get(index))).get(key);
				return value;
			}else{
				return "";
			}
		}
		return "";
	}
	
	/**
	 * Get a string "value" of JSONObject named "key" or get default value if value is null or EMPTY!
	 * @param jo - JSONObject to get data from
	 * @param key - fied in JSON
	 * @param defaultString - default replacement if null or empty
	 */
	public static String getStringOrDefault(JSONObject jo, String key, String defaultString){
		String res = defaultString;
		if (jo != null && jo.containsKey(key)){
			res = (String) jo.get(key);
			if (Is.nullOrEmpty(res)){
				res = defaultString;
			}
		}
		return res;
	}
	
	/**
	 * Get an Object located inside a nested JSONObject by following the 'pathOfKeys' step-by-step.
	 * Returns null if the path breaks somewhere.
	 */
	public static Object getObject(JSONObject jObject, String[] pathOfKeys){
		try{
			for (int i=0; i<(pathOfKeys.length-1); i++){
				jObject = (JSONObject) jObject.get(pathOfKeys[i]);
			}
			return jObject.get(pathOfKeys[pathOfKeys.length-1]);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * Get a JSONObject from another JSONObject at "key".
	 * 
	 * @param j_obj - JSONObject input
	 * @param key - key field in jObject 
	 * @return JSONObject or null
	 */
	public static JSONObject getJObject(JSONObject j_obj, String key){
		return (JSONObject) (j_obj.get(key));
	}
	/**
	 * Get a JSONObject located inside a nested JSONObject by following the 'pathOfKeys' step-by-step.
	 * Returns null if the path breaks somewhere.
	 */
	public static JSONObject getJObject(JSONObject jObject, String[] pathOfKeys){
		try{
			for (int i=0; i<pathOfKeys.length; i++){
				jObject = (JSONObject) jObject.get(pathOfKeys[i]);
			}
			return jObject;
		}catch(Exception e){
			return null;
		}
	}
	/**
	 * Get a JSONObject from JSONArray at "index".
	 * Long: If you have an JSONArray and you want the JSONObject at a certain index of the array than you can get it with this method ;-)
	 * 
	 * @param j_array - JSONArray input
	 * @param index - array position in JSONArray 
	 * @return value as String or null
	 */
	public static JSONObject getJObject(JSONArray j_array, int index){
		JSONObject value;
		value = (JSONObject) (j_array.get(index));
		return value;
	}
	
	/**
	 * Get a JSONArray at "key" position, located inside a JSONObject that is given as string (phu, that sounds complicated).
	 * @param jString - JSONObject given as string
	 * @param key - key name of the field where the array is expected
	 * @return JSONArray or throws error
	 */
	public static JSONArray getJArray(String jString, String key){
		try {
			JSONObject jo = parseStringOrFail(jString);
			JSONArray ja = (JSONArray) jo.get(key);
			return ja;
		} catch (Exception e) {
			throw new RuntimeException(DateTime.getLogDate() + " WARNING - JSON.java / getJArray() - Failed to get jArray at key (" + key + ") or parse JSON string: " + jString, e);
		}
	}
	/**
	 * Get a JSONArray located inside a nested JSONObject by following the 'pathOfKeys' step-by-step.
	 * Returns null if the path breaks somewhere.
	 */
	public static JSONArray getJArray(JSONObject jObject, String[] pathOfKeys){
		try{
			for (int i=0; i<(pathOfKeys.length-1); i++){
				jObject = (JSONObject) jObject.get(pathOfKeys[i]);
			}
			return (JSONArray) jObject.get(pathOfKeys[pathOfKeys.length-1]);
		}catch(Exception e){
			return null;
		}
	}
	/**
	 * Get a JSONArray from another JSONObject at "key".
	 * 
	 * @param j_obj - JSONObject input
	 * @param key - key field in jObject 
	 * @return JSONArray or null
	 */
	public static JSONArray getJArray(JSONObject j_obj, String key){
		return (JSONArray) (j_obj.get(key));
	}
	
	/**
	 * Sort an JSONArray by a primary and secondary key. Values should be strings in this case.
	 * @param jArray - JSONArray to sort
	 * @param primaryKey - primary sort key
	 * @param secondaryKey - secondary sort key or empty string
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray sortArrayByString(JSONArray jArray, String primaryKey, String secondaryKey){
		Collections.sort(jArray, new Comparators.JsonStringValues(primaryKey, secondaryKey));
		return jArray;
	}
	/**
	 * Inverse sorting of an JSONArray by a primary and secondary key. Values should be strings in this case.
	 * @param jArray - JSONArray to sort
	 * @param primaryKey - primary sort key
	 * @param secondaryKey - secondary sort key or empty string
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray sortArrayInverseByString(JSONArray jArray, String primaryKey, String secondaryKey){
		Collections.sort(jArray, Collections.reverseOrder(new Comparators.JsonStringValues(primaryKey, secondaryKey)));
		return jArray;
	}
	/**
	 * Sort an JSONArray by a primary and secondary key. Values should be long in this case.
	 * @param jArray - JSONArray to sort
	 * @param primaryKey - primary sort key
	 * @param secondaryKey - secondary sort key or empty string
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray sortArrayByLong(JSONArray jArray, String primaryKey, String secondaryKey){
		Collections.sort(jArray, new Comparators.JsonLongValues(primaryKey, secondaryKey));
		return jArray;
	}
	/**
	 * Inverse sorting of an JSONArray by a primary and secondary key. Values should be long in this case.
	 * @param jArray - JSONArray to sort
	 * @param primaryKey - primary sort key
	 * @param secondaryKey - secondary sort key or empty string
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray sortArrayInverseByLong(JSONArray jArray, String primaryKey, String secondaryKey){
		Collections.sort(jArray, Collections.reverseOrder(new Comparators.JsonLongValues(primaryKey, secondaryKey)));
		return jArray;
	}
	
	/**
	 * Take a  List of strings and make a JSONArray out of it.
	 * @return JSONArray
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray stringListToJSONArray(List<String> list){
		JSONArray array = new JSONArray();
		for (String s : list){
			array.add(s);
		}
		return array;
	}
	/**
	 * Take a List of objects and make a JSONArray out of it.
	 * @return JSONArray
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray objectListToJSONArray(List<Object> list){
		JSONArray array = new JSONArray();
		for (Object o : list){
			array.add(o);
		}
		return array;
	}
	
	/**
	 * Take a Collection of strings and make a JSONArray out of it.
	 * @return JSONArray
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray stringCollectionToJSONArray(Collection<String> collection){
		JSONArray array = new JSONArray();
		for (String s : collection){
			array.add(s);
		}
		return array;
	}
	/**
	 * Take a Collection of objects and make a JSONArray out of it.
	 * @return JSONArray
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray objectCollectionToJSONArray(Collection<Object> collection){
		JSONArray array = new JSONArray();
		for (Object o : collection){
			array.add(o);
		}
		return array;
	}
	
	/**
	 * Simple put. Just putting an object with key to JSONObject
	 * @param obj - JSONObject to add stuff to
	 * @param key - key value
	 * @param add - object to put at key position
	 * @return JSONObject with added key value pair
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject put(JSONObject obj, String key, Object add){
		obj.put(key, add);
		return obj;
	}
	/**
	 * Set the value of a certain field by following the path given with dots in 'key', e.g.<br>
	 * level1.level2.key -> { "level1" : { "level2" : { "key" : value } } }.<br>
	 * This will add new fields to the JSONObject as required.
	 * @param obj - JSONObject to add data to
	 * @param key - path given with dots or just a key
	 * @param value - value to write at the end of the path
	 * @return the given JSONObject with added data
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject putWithDotPath(JSONObject obj, String key, Object value){
		if(key.contains(".")){
			String[] path = key.split("\\.");
			if (path.length == 2){
				if (obj.containsKey(path[0])){
					((JSONObject) obj.get(path[0])).put(path[1], value);
				}else{
					obj.put(path[0], make(path[1], value));
				}
			}else{
				if (obj.containsKey(path[0])){
					putWithPath((JSONObject) obj.get(path[0]), Arrays.copyOfRange(path, 1, path.length), value);
				}else{
					JSONObject nextObj = new JSONObject();
					obj.put(path[0], nextObj);
					putWithPath(nextObj, Arrays.copyOfRange(path, 1, path.length), value);
				}
			}
		}else{
			obj.put(key, value);
		}
		return obj;
	}
	/**
	 * Set the value of a certain field by following the given 'path', e.g.<br>
	 * path[0]="level1", path[1]="level2" -> { "level1" : { "level2" : value } }.<br>
	 * Works well together with 'putWithDotPath'.<br>
	 * This will add new fields to the JSONObject as required.
	 * @param obj - JSONObject to add data to
	 * @param path - path given as array
	 * @param value - value to write at the end of the path
	 * @return the given JSONObject with added data
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject putWithPath(JSONObject obj, String[] path, Object value){
		if (path.length == 2){
			if (obj.containsKey(path[0])){
				((JSONObject) obj.get(path[0])).put(path[1], value);
			}else{
				obj.put(path[0], make(path[1], value));
			}
		}else{
			if (obj.containsKey(path[0])){
				putWithPath((JSONObject) obj.get(path[0]), Arrays.copyOfRange(path, 1, path.length), value);
			}else{
				JSONObject nextObj = new JSONObject();
				obj.put(path[0], nextObj);
				putWithPath(nextObj, Arrays.copyOfRange(path, 1, path.length), value);
			}
		}
		return obj;
	}
	/**
	 * Simple add (same as put, ... historic reasons ...). 
	 * Just adding an object with key to JSONObject
	 * @param obj - JSONObject to add stuff to
	 * @param key - key value
	 * @param add - object to add at key position
	 * @return JSONObject with added key value pair
	 */
	public static JSONObject add(JSONObject obj, String key, Object add){
		return put(obj, key, add);
	}
	/**
	 * Simple add. Just adding an object with key to JSONArray
	 * @param a - JSONArray to add stuff to
	 * @param add - object to add
	 * @return JSONArray with added object
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray add(JSONArray a, Object add){
		a.add(add);
		return a;
	}
	/**
	 * Simple add. Just adding an object with key to JSONArray
	 * @param a - JSONArray to add stuff to
	 * @param adds - objects to add
	 * @return JSONArray with added object
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray add(JSONArray a, Object... adds){
		for (Object o : adds){
			a.add(o);
		}
		return a;
	}
	/**
	 * Append one array to another.
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray addAll(JSONArray start, JSONArray append){
		start.addAll(append);
		return start;
	}
	/**
	 * Add if the value is not null
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject addIfNotNull(JSONObject obj, String key, Object value){
		if (value != null){
			obj.put(key, value);
		}
		return obj;
	}
	/**
	 * Add if the value is not null or empty.
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject addIfNotNullOrEmpty(JSONObject obj, String key, String value){
		if (value != null && !value.isEmpty()){
			obj.put(key, value);
		}
		return obj;
	}
	
	/**
	 * Makes a complex, deep JSONObject as flat as possible, saving all key-value-pairs on the top level by creating a string path for every key,
	 * like a:{b:..{c:...}} = a.b.c
	 * Best way to understand this is just try and see ^^. For 'null' values the keys are removed. 
	 * @param obj - JSONObject to start with
	 * @param key_prefix - as this method is recursive it has to pass down info about the top level. Start with "" or null.
	 * @param result - the intermediate result is passed down too, start with null.
	 * @return Flat Eric :-)
	 */
	public static JSONObject makeFlat(JSONObject obj, String key_prefix, JSONObject result){
		if (obj == null){		return null;		}
		if (result == null){	result = new JSONObject();		}
		if (obj.isEmpty()){		return result;		}
		//start to iterate
		Iterator<?> it = obj.keySet().iterator();
		while( it.hasNext() ) {
		    String key = (String) it.next();	key = key.trim();
		    //System.out.println("key=" + key + " val=" + ((o==null)? "" : o.toString())); 	//debug
		    Object o = obj.get(key);
		    //is the entry a JSONObject itself?
		    if (o!= null && o.getClass().equals(result.getClass())){
		    	if (key_prefix !=null && !key_prefix.isEmpty()){
		    		makeFlat((JSONObject) o, key_prefix + "." + key, result);
		    	}else{
		    		makeFlat((JSONObject) o, key, result);
		    	}
		    }
		    //no then add it
		    else if (o!=null){
		    	if (key_prefix !=null && !key_prefix.isEmpty()){
		    		JSON.add(result, key_prefix + "." + key, o);
		    	}else{
		    		JSON.add(result, key, o);
		    	}
		    }
		}
		return result;
	}
	
	/**
	 * TODO: test! <br>
	 * Merge "source" into "target". If fields have equal name, merge them recursively.
	 * @return the merged object (target).
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject deepMerge(JSONObject source, JSONObject target){
	    for (Object key: source.keySet()){
	            Object value = source.get(key);
	            if (!target.containsKey(key)){
	                // new value for "key":
	                target.put(key, value);
	            }else{
	                // existing value for "key" - recursively deep merge:
	                if (value instanceof JSONObject){
	                    JSONObject valueJson = (JSONObject)value;
	                    deepMerge(valueJson, (JSONObject) target.get(key));
	                }else{
	                    target.put(key, value);
	                }
	            }
	    }
	    return target;
	}
	
	/**
	 * Write a JSONObject to a file (UTF-8 encoding).
	 * @param filePath - path including file name
	 * @param obj - JSONObject
	 * @return true/false
	 */
	public static boolean writeJsonToFile(String filePath, JSONObject obj){
		if (obj == null){
			return false;
		}
		//try (FileWriter file = new FileWriter(filePath)) {
		try (OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);){
			//file.write(obj.toJSONString());
			w.write(obj.toJSONString());
			return true;
		}catch (Exception e){
			System.err.println(DateTime.getLogDate() + " ERROR - JSON.java / writeJsonToFile() - Failed to write: " + filePath + " - MSG: " + e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}
	/**
	 * Read a JSONObject from file (UTF-8 encoding).
	 * @param filePath - path including file name
	 * @return JSONObject or null
	 */
	public static JSONObject readJsonFromFile(String filePath){
		try (Reader r = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);) {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(r);
            //Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject;
 
        } catch (Exception e) {
        	System.err.println(DateTime.getLogDate() + " ERROR - JSON.java / readJsonFromFile() - Failed to read: " + filePath + " - MSG: " + e.getMessage());
            //e.printStackTrace();
        	return null;
        }
	}

}
