package net.b07z.sepia.server.core.assistant;

/**
 * Collection of available client tags.
 * 
 * @author Florian Quirin
 *
 */
public class CLIENTS {
	
	//this is somewhat identical with ENVIRONMENTS but we keep it anyway ^^
	final public static String WEB_APP = "web_app";				//e.g. HTML Browser
	final public static String BROWSER = "browser";				//e.g. HTML Browser (alternative to web app)
	final public static String HTML_APP = "html_app";			//e.g. HTML based app that runs like a "real" app
	final public static String ANDROID_APP = "android_app";		//e.g. Android phone
	final public static String IOS_APP = "ios_app";				//e.g. iPhone, iPad
	final public static String IOS_BROWSER = "ios_browser";		//e.g. iPhone, iPad safari
	final public static String WIN_APP = "windows_app";			//e.g. windows app store app for mobile or desktop
	final public static String JAVA_APP = "java_app";			//e.g. VW TTS Client
	final public static String CHROME_BROWSER = "chrome_browser";
	final public static String CHROME_APP = "chrome_app";
	final public static String SAFARI_BROWSER = "safari_browser";
	final public static String SAFARI_APP = "safari_app";
	
	/**
	 * Check for environment by comparing the input environment (this_env) to an array of
	 * test environments (test_envies). E.g. you want to check for a web app running on iOS use:
	 * compare(this_env, ENVIRONMENTS.WEB_APP, ENVIRONMENTS.iOS).
	 * 
	 * @param this_env - input environment
	 * @param test_envies - array of tests
	 * @return true/false
	 */
	static boolean compare(String this_env, String... test_envies){
		String test = "";
		if (test_envies.length == 1){
			test = test_envies[0].toLowerCase();
		}else{
			for (String t : test_envies){
				test = test + "|" + t.toLowerCase();
			}
			test = test.replaceFirst("^\\|", "").trim();
		}
		if (this_env.toLowerCase().matches(".*(" + test + ").*")){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Is this client simply a browser (not in stand alone or app mode).
	 */
	public static boolean isWebSite(String client){
		return compare(client, WEB_APP, BROWSER);
	}
	
	/**
	 * Has this client a way to show HTML data in a WebView or simply a browser tab?
	 */
	public static boolean hasWebView(String client){
		return compare(client, WEB_APP, BROWSER, HTML_APP, IOS_APP, ANDROID_APP, WIN_APP);
	}
	
	/**
	 * Can the client handle answer texts with HTML code?
	 */
	public static boolean isTextViewHtmlCompatible(String client){
		return compare(client, WEB_APP, BROWSER, HTML_APP);
	}
	
	/**
	 * Can this client play OGG files?
	 */
	public static boolean canPlayOGG(String client){
		return compare(client, ANDROID_APP, CHROME_BROWSER, CHROME_APP);
	}
	
	/**
	 * Can this client for example use apple maps?
	 */
	public static boolean isAppleCompatible(String client){
		return compare(client, IOS_APP, IOS_BROWSER, SAFARI_BROWSER, SAFARI_APP);
	}
	
	/**
	 * Better store tokens only for a few hours?
	 */
	public static boolean isRatherUnsafe(String client){
		return compare(client, WEB_APP, BROWSER);
	}

}
