package net.b07z.sepia.server.core.tools;

import java.util.Arrays;

/**
 * Command-line interface to run different tools.
 * 
 * @author Florian Quirin
 *
 */
public class ToolsRunner {

	/**
	 * Run tools.
	 * @param args
	 */
	public static void main(String[] args) {
		//Help
		if (args == null || args.length == 0 || args[0].equals("-h") || args[0].equals("-help") || args[0].equals("help")){
			help();
			return;
		
		//Tools to run:
		}else if (args[0].equals("connection-check")){
			String[] toolArgs = Arrays.copyOfRange(args, 1, args.length);
			ConnectionCheck.main(toolArgs);
			return;
		
		//Unknown
		}else{
			System.out.println("Unknown tool: " + args[0]);
			help();
			return;
		}
	}
	
	/**
	 * Command-line interface help.
	 */
	private static void help(){
		System.out.println("\nUsage:");
		System.out.println("[tool] [arguments]");
		System.out.println("\nTools:");
		System.out.println("connection-check - args: -h");
		System.out.println("");
	}

}
