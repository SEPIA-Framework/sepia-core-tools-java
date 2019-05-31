package net.b07z.sepia.server.core.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Some convenience methods to work with strings.
 * 
 * @author Florian Quirin
 *
 */
public class StringTools {

	/**
	 * Find first matching regular expression or return empty string.
	 * @param input - input to search in
	 * @param regEx - regular expression to search for
	 */
	public static String findFirstRexEx(String input, String regEx){
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()){
			return matcher.group(0);
		}else{
			return "";
		}
	}
	
	/**
	 * Find all groups of matching regular expression or return empty list.
	 * @param input - input to search in
	 * @param regEx - regular expression to search for
	 */
	public static List<String> findAllRexEx(String input, String regEx){
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(input);
		List<String> matches = new ArrayList<>();
		while (matcher.find()){
			matches.add(matcher.group(0));
		}
		return matches;
	}
}
