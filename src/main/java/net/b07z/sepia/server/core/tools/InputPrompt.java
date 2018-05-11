package net.b07z.sepia.server.core.tools;

import java.util.Scanner;

/**
 * Convenience methods to get user input via console.
 * 
 * @author Florian Quirin
 *
 */
public class InputPrompt {

	/**
	 * Ask for a string input.
	 * @param comment - Comment to show before input
	 * @param closeAfterwards - close System.in after input? Note that you cannot use it anymore then.
	 * @return input as string
	 */
	public static String askString(String comment, boolean closeAfterwards){
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println(comment);
		String in = reader.next();
		if(closeAfterwards){
			reader.close(); 	//Closes System.in for rest of session O_O
		}
		return in;
	}
}
