package net.b07z.sepia.server.core.assistant;

/**
 * Statics holding all possible environments.
 * 
 * @author Florian Quirin
 *
 */
public class ENVIRONMENTS {
	
	final public static String DEFAULT = "default"; 				//default environment with all options to display results, e.g. smartphone
	final public static String AVATAR_DISPLAY = "avatar_display";	//avatar display with limited space to display results, sound available
	final public static String SPEAKER = "speaker";					//smart speaker with audio only
	final public static String SMART_SPEAKER = "smart_speaker";		//smart speaker with audio only
	final public static String SMART_DISPLAY = "smart_display";		//smart speaker with display and audio
	final public static String SILENT_DISPLAY = "silent_display";	//display without audio
	final public static String CAR_DISPLAY = "car_display";			//display with audio. Might have modified behavior to for events, follow-ups, etc. TBD
	//watch ?
	//...

	/**
	 * Check if the device has a proper display in correct mode. 
	 * @param environment - 'env' state submitted by SEPIA clients
	 */
	public static boolean deviceHasActiveDisplay(String environment){
		environment = environment.toLowerCase();
		if (environment.contains(AVATAR_DISPLAY)){
			return false;		//if the avatar display is active, space is too limited - this might change in future
		}else if (environment.equals(SPEAKER) || environment.equals(SMART_SPEAKER)){
			return false;
		}else{
			return true;
		}
	}
}
