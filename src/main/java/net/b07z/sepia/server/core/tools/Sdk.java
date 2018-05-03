package net.b07z.sepia.server.core.tools;

/**
 * Tools that are made specifically for the SDK and can be used in other APIs too.
 * 
 * @author Florian Quirin
 *
 */
public class Sdk {
	
	/**
	 * Names of custom commands should start with the user ID. Using this method inside the custom service that defines the command
	 * should give the correct name.
	 * @param clazz - typically 'this'
	 * @param cmdName - the name you wish to use
	 * @return
	 */
	public static String getMyCommandName(Object clazz, String cmdName){
		return (clazz.getClass().getPackage().getName().replaceAll(".*\\.", "") + "." + cmdName);
	}

}
