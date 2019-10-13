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
	final public static String SILENT_DISPLAY = "silent_display";	//display without audio
	final public static String CAR_DISPLAY = "car_display";			//display with audio. Might have modified behavior to for events, follow-ups, etc. TBD
	//watch ?
	//...

}
