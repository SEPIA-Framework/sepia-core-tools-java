package net.b07z.sepia.server.core.tools;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Converters mostly from "something" to JSON and other helper methods like type conversions and even a random generator.
 * 
 * @author Florian Quirin
 *
 */
public class Converters {
	
	/**
	 * Take a sentence (or multiple) and remove all special characters so you can use it as a database ID. 
	 * @param sentence - a sentence or multiple separated by "."
	 * @return clean string that can be used as ID
	 */
	public static String makeIDfromSentence(String sentence){
		String id = sentence.replaceAll("\\.\\s", "__");
		id = id
			.replaceAll("\\s+", "_")
			.replaceAll("İ", "i")
			.toLowerCase()
			.replaceAll("ö", "oe").replaceAll("ä", "ae").replaceAll("ü", "ue")
			.replaceAll("\\W", "")
			.trim();
		//TODO: what if it is empty now?
		
		return id;
	}
	
	/**
	 * Remove all non-alphabetic and non-digit characters
	 */
	public static String cleanString(String text){
		return (text.replaceAll("[^\\p{IsAlphabetic}^\\p{IsDigit}]", "").trim());
	}

	/**
	 * Create the "cmd_summary" from "command" and "params" that can be used to call the Answer-API.
	 * @param command - command class string
	 * @param params - JSONObject with parameters for command
	 * @return "cmd_summary" string
	 */
	public static String makeCommandSummary(String command, JSONObject params){
		if (command == null || command.isEmpty() || params == null){
			return "";
		}
		String seperator = ";;";
		String cmd_summary = command + seperator;
		String add = "";
		try{
			for(Iterator<?> iterator = params.keySet().iterator(); iterator.hasNext();) {
				String k = iterator.next().toString().trim();
			    String p = k.replaceAll("^<|>$", "").trim();
			    String v = params.get(k).toString().trim();
			    if (!p.isEmpty() && !v.isEmpty()){
					add += p + "=" + v + seperator;
				}
			}
		}catch (Exception e){
			add = "";
		}
		cmd_summary += add.trim();
		return cmd_summary;
	}
	/**
	 * Get parameters from "cmd_summary" string.
	 * @param cmd - command (must be known)
	 * @param cmdSummary - whole string of cmd_summary
	 * @return JSONObject with parameters or null (if none are found)
	 */
	public static JSONObject getParametersFromCommandSummary(String cmd, String cmdSummary){
		cmdSummary = cmdSummary.replaceFirst(";;$", "").trim();
		if (cmdSummary.matches("^" + Pattern.quote(cmd + ";;") + ".+")){
			String paramString = cmdSummary.split(Pattern.quote(cmd + ";;"), 2)[1];
			String[] paramsPV = paramString.trim().split(";;(?=[\\w.-]+=)");
			JSONObject params = new JSONObject();
			for (int i=0; i<paramsPV.length; i++){
				String[] pv = paramsPV[i].split("=", 2);
				JSON.put(params, pv[0], pv[1]);
			}
			return params;
		}else{
			return null;
		}
	}
	
	/**
	 * Convert to UTF-8 url string.
	 * @param in
	 * @return
	 */
	public static String url_utf8(String in){
		try {
			return URLEncoder.encode(in, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	/**
	 * Take an object where you are SURE! it is a valid double, either in string or double format, round it to 1% precision 
	 * and convert it back to string without trailing zeros.
	 * @param val - value to round, is object but MUST BE a valid double
	 * @param round_to_int - ignore precision and round to integer?
	 * @return rounded double as string or empty
	 */
	public static String smartRound(Object val, boolean round_to_int){
		double v = obj2DoubleOrDefault(val, Double.NEGATIVE_INFINITY);
		if (v == Double.NEGATIVE_INFINITY){
			System.err.println(DateTime.getLogDate() + " ERROR - Converters.java smartRound(..) FAILED! with: " + val.toString());
			return "";
		}else{
			try {
				String r;
				if (v >= 100 || round_to_int){
					r = Double.toString(Math.round(v));
				}else if (v >= 10){
					r = Double.toString(Math.round(v * 10.0d)/10.0d);
				}else if (v >= 1){
					r = Double.toString(Math.round(v * 100.0d)/100.0d);
				}else{
					//TODO: fix this - it should do 0.071124754000 -> 0.0711
					r = Double.toString(v);
				}
				r = r.replaceFirst("(\\.|,)(.*)(0+$)", "$1$2").trim().replaceFirst("(\\.|,)$", "").trim();
				return r;
			}catch (Exception e){
				return "";
			}
		}
	}
	
	/**
	 * Return the default expected format for decimal numbers:
	 * <li>Round half-up (1.5 rounds to 2)</li>
	 * <li>Decimal separator is dot "."</li>
	 * <li>Grouping separator is empty</li>
	 */
	public static DecimalFormat getDefaultDecimalFormat(){
		DecimalFormat df = new DecimalFormat();
		df.setRoundingMode(RoundingMode.HALF_UP);
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(Character.MIN_VALUE);
		df.setDecimalFormatSymbols(symbols);
		return df;
	}
	/**
	 * Convert a string to {@link Number} using the default decimal format {@link #getDefaultDecimalFormat()}.<br>
	 * NOTE:
	 * <li>String MUST NOT have a grouping separator (2,000.0 has to be 2000.0)</li>
	 * <li>Decimal separator MUST BE dot "."</li>
	 * @param num - String in format described above
	 * @return Number or Exception
	 * @throws java.text.ParseException
	 */
	public static Number stringToNumber(String num) throws java.text.ParseException{
		return getDefaultDecimalFormat().parse(num);
	}
	/**
	 * Convert {@link Number} using the default decimal format {@link #getDefaultDecimalFormat()} to a string with custom pattern.
	 * You can use {@link #stringToNumber(String)} if you have a String instead of a Number as input. 
	 * @param num - Number
	 * @param pattern - e.g. "#.##" or "00.00". NOTE: use dot "." as separator!
	 * @return
	 */
	public static String numberToString(Number num, String pattern) throws IllegalArgumentException{
		DecimalFormat df = getDefaultDecimalFormat();
		df.applyPattern(pattern);
		return df.format(num);
	}
	
	/**
	 * Convert an unknown object to String or return null.
	 * @param in - object to convert
	 * @param def - default
	 * @return String or default
	 */
	public static String obj2StringOrDefault(Object in, String def){
		try {
			return in.toString();
		} catch (Exception e){
			return def;
		}
	}
	/**
	 * Convert an unknown object that holds a double (real double or string double) to double.
	 * @param in - string to convert, must be in double format
	 * @param def - default
	 * @return double value or default
	 */
	public static double obj2DoubleOrDefault(Object in, Double def){
		try {
			return Double.parseDouble((String.valueOf(in)));
		} catch (Exception e){
			return def;
		}
	}
	/**
	 * Convert an unknown object that holds a double (real double or string double) to long.
	 * @param in - string to convert, must be in double format
	 * @param def - default
	 * @return long value or default
	 */
	public static long obj2LongOrDefault(Object in, Long def){
		try {
			return (long) Double.parseDouble((String.valueOf(in)));
		} catch (Exception e){
			return def;
		}
	}
	/**
	 * Convert an unknown object that holds an integer/double (real type or as string) to an integer.
	 * Removes all non-numbers (except .,+-) in the string!
	 * @param in - string to convert, must be in double format
	 * @param def - default
	 * @return int value or def
	 */
	public static int obj2IntOrDefault(Object in, Integer def){
		try {
			return (int) Double.parseDouble((String.valueOf(in).replaceAll("[^\\d\\.,\\-\\+]", "")));
		} catch (Exception e){
			return def;
		}
	}
	
	/**
	 * Convert from string to JSON Object
	 * @param s - String to parse
	 * @return JSONObject
	 */
	public static JSONObject str2Json(String s){
		JSONParser parser = new JSONParser();
		JSONObject result;
		try {
			result = (JSONObject) parser.parse(s);
			return result;
		} catch (ParseException e) {
			return null;
			//e.printStackTrace();
		}
	}
	
	/**
	 * Convert a default (SEPIA) data string to a JSON object.<br>
	 * The data string has been replaced in most cases with JSON when possible.
	 * @param dataString - standard (for this framework) formatted data string, e.g. &#60city&#62Berlin&#60country&#62Germany
	 * @return JSONObject with keys mapped or empty JSONObject
	 */
	public static JSONObject dataString_2_JSON(String dataString){
		String[] s1 = dataString.split("<");
		String[] s2;
		if (s1.length > 1){
			JSONObject js = new JSONObject();
			for (String s : s1){
				s2 = s.split(">",2);
				if (s2.length == 2){
					JSON.add(js, s2[0], s2[1]);
				}
			}
			return js;
		
		}else{
			return new JSONObject();
		}
	}
	/**
	 * Get attribute (city, first name, whatever ...) from a data string in standard format (&#60city&#62Berlin&#60country&#62Germany).
	 * Data strings are the short version of certain account entries like addresses, contacts, names (first, last, nick...) etc. and
	 * might be used inside the database itself or created during the read process. If nothing is found returns empty string.
	 * @param dataString - standard (for this framework) formatted data string
	 * @param key - key string to search for. NOTE: if the key contains a dot "." like "uname.firstN" everything before WILL BE REMOVED!
	 * @return value to key or empty string
	 */
	@Deprecated
	public static String dataStringGetAttribute(String dataString, String key){
		String value;
		if (key.contains(".")){
			key = key.replaceFirst(".*\\.", "").trim();
		}
		if (dataString != null && dataString.contains("<"+ key +">")){
			value = dataString.replaceAll(".*<"+ key +">(.*?)(<.*|$)", "$1").trim();
			return value;
		}else{
			return "";
		}
	}
	/**
	 * Add attribute (city, first name, ...) to data string. See getAttribute(...) for more info.
	 * If the data string is null or empty a new one is created. 
	 * @param dataString - standard (for this framework) formatted data string
	 * @param key - key string (attribute) to add or replace. NOTE: if the key contains a dot "." like "uname.firstN" everything before WILL BE REMOVED!
	 * @param value - value to add for this attribute
	 * @return modified/updated data string
	 */
	@Deprecated
	public static String dataStringSetAttribute(String dataString, String key, String value){
		if (key.contains(".")){
			key = key.replaceFirst(".*\\.", "").trim();
		}
		if (dataString != null && dataString.contains("<"+ key +">")){
			dataString = dataString.replaceAll("(<"+ key +">).*?(<.*|$)", "$1" + value + "$2").trim();
		}else if (dataString != null && !dataString.isEmpty()){
			dataString += "<"+ key +">" + value;
		}else{
			dataString = "<"+ key +">" + value;
		}
		return dataString;
	}/**
	 * Add attributes in a HashMap (city, first name, ...) to data string. See getAttribute(...) for more info.
	 * If the data string is null or empty a new one is created. 
	 * @param dataString - standard (for this framework) formatted data string
	 * @param map - HashMap<String, Object> used to fill the data string
	 * @return modified/updated data string
	 */
	@Deprecated
	public static String dataStringSetAttribute(String dataString, HashMap<String, Object> map){
		if (map != null){
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String k = entry.getKey();
				Object o = entry.getValue();
				if (k != null && o != null){
					dataString = dataStringSetAttribute(dataString, k, o.toString());
				}
			}
		}
		return dataString;
	}
	
	/**
	 * Converts a Map&lt;String,String&gt; to a JSONObject
	 * 
	 * @param m - Map&lt;String, String&gt;
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject mapStrStr2Json(Map<String, String> m){
		JSONObject params = new JSONObject();
		for (Map.Entry<String, String> entry : m.entrySet()) {
			String p = entry.getKey();
			String v = entry.getValue();
			//System.out.println(entry.getKey() + "/" + entry.getValue());
			params.put(p, v);
		}
		return params;
	}
	/**
	 * Converts a Map&lt;String,Long&gt; to a JSONObject
	 * 
	 * @param m - Map&lt;String, Long&gt;
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject mapStrLng2Json(Map<String, Long> m){
		JSONObject params = new JSONObject();
		for (Map.Entry<String, Long> entry : m.entrySet()) {
			String p = entry.getKey();
			Long v = entry.getValue();
			//System.out.println(entry.getKey() + "/" + entry.getValue());
			params.put(p, v);
		}
		return params;
	}
	/**
	 * Converts a Map&lt;String,Object&gt; to a JSONObject
	 * 
	 * @param m - Map&lt;String, Object&gt;
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject mapStrObj2Json(Map<String, Object> m){
		JSONObject params = new JSONObject();
		for (Map.Entry<String, Object> entry : m.entrySet()) {
			String p = entry.getKey();
			Object v = entry.getValue();
			//System.out.println(entry.getKey() + "/" + entry.getValue());
			params.put(p, v);
		}
		return params;
	}
	
	/**
	 * Converts a Map&lt;String,String&gt; to a single string separated by ";;"
	 * 
	 * @param m - Map&lt;String, String&gt;
	 * @return
	 */
	public static String mapStrStr2Str(Map<String, String> m){
		String result="";
		for (Map.Entry<String, String> entry : m.entrySet()) {
			String p = entry.getKey();
			String v = entry.getValue();
			result += p + "=" + v + ";;";
		}
		return result.trim();
	}
	
	/**
	 * Convert JSONObject to HashMap&lt;String,String&gt; by transferring all TOP-LEVEL key-value pairs. All values should be at least convertible to "String".
	 * Nested values are converted to strings.
	 * @param jsonObject - simple JSON object
	 * @return HashMap&lt;String,String&gt; (can be empty)
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> json2HashMapStrStr(JSONObject jsonObject) {
		Map<String,String> params = new HashMap<>();
		if (jsonObject == null) {
			return params;
		} else {
			for (Object entry : jsonObject.entrySet()) {
				Map.Entry<String, Object> entryObj = (Map.Entry<String, Object>) entry;
				params.put(entryObj.getKey(), entryObj.getValue().toString());
			}
		}
		return params;
	}
	/**
	 * Convert JSONObject to HashMap&lt;String,Object&gt; by unchecked cast.
	 * Nested JSONObjects remain what they are.
	 * @param jsonObject - simple JSON object
	 * @return HashMap&lt;String,Object&gt; (can be empty)
	 */
	public static Map<String, Object> json2HashMap(JSONObject jsonObject) {
		return object2HashMapStrObj(jsonObject);
	}
	/**
	 * Add content of JSONObject to a Map converting all values to String.
	 * @param jsonSource - source JSONObject
	 * @param targetMap - target Map (non null!)
	 */
	@SuppressWarnings("unchecked")
	public static void addJsonToMapAsStrings(JSONObject jsonSource, Map<String, String> targetMap){
		for (Object entry : jsonSource.entrySet()) {
			Map.Entry<String, Object> entryObj = (Map.Entry<String, Object>) entry;
			targetMap.put(entryObj.getKey(), entryObj.getValue().toString());
		}
	}
	
	/**
	 * Convert a JSONArray to an ArrayList of strings.
	 * @param jArray - array to convert, entries are cast to String via '.toString()'
	 */
	public static List<String> jsonArrayToStringList(JSONArray jArray){
		List<String> list = new ArrayList<>();
		for (Object o : jArray){
			list.add(o.toString());
		}
		return list;
	}
	
	/**
	 * Makes an unchecked (cause you can't check it, can you?) cast from Object to HashMap&#60String, Object&#62.
	 * @param input - object that is supposed to be the expected HashMap 
	 * @return HashMap or null
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> object2HashMapStrObj(Object input){
		try {
			return (HashMap<String, Object>) input;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Makes an unchecked (cause you can't check it, can you?) cast from Object to ArrayList&#60Object&#62.
	 * @param input - object that is supposed to be the expected ArrayList 
	 * @return ArrayList or null
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> object2ArrayListObj(Object input){
		try {
			return (ArrayList<Object>) input;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Makes an unchecked (cause you can't check it, can you?) cast from Object to ArrayList&#60String&#62.
	 * @param input - object that is supposed to be the expected ArrayList 
	 * @return ArrayList or null
	 */
	@SuppressWarnings("unchecked")
	public static List<String> object2ArrayListStr(Object input){
		try {
			return (ArrayList<String>) input;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Removes HTML code from an answer - no guarantee and no completeness! Plz check carefully!
	 * I'd prefer to let the specific client do this.
	 * 
	 * @param input - string to relieve from HTML code
	 * @return
	 */
	public static String removeHTML(String input){
		String out = input;
		out = out.replaceAll("(<div .*?>|<a .*?>|<p .*?>|<span .*?>|<img .*?>)", "");
		out = out.replaceAll("(<div>|<a>|<img>|<p>|<span>)", " ");
		out = out.replaceAll("(</div>|</a>|</img>|<br>|</p>|</span>)", " ");
		out = out.replaceAll("( )+", " ");
		return out;
	}
	
	/**
	 * Get a random number between start (inclusive) and end. End must be >= start.
	 * @param start - start with this number (inclusive)
	 * @param end - end with this number (exclusive)
	 * @return random integer in range
	 */
	public static int randomInt(int start, int end){
		if (end < start){
			end = start+1;
		}
		int res = new Random().nextInt(end-start) + start;
		return res;
	}
	/**
	 * Get a random value of 10, 100 or 1000 with a higher probability of hitting 10 (4) and 100 (2) than 1000 (1).
	 * @return random integer either 10, 100 or 1000
	 */
	public static int random_10_100_1000(){
		int[] sel = {10,10,10,10,100,100,1000};
		int n = new Random().nextInt(sel.length);
		return sel[n];
	}
}
