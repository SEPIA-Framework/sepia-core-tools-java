package net.b07z.sepia.server.core.tools;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Used to debug code and log info.<br><br>
 * Types:<br>
 * 1:	ERROR<br>
 * 2:	INFO<br>
 * 3:	LOG<br>
 * <br>Also includes a tic-toc method and other helpers.
 * @author Florian Quirin
 *
 */
public class Debugger {
	
	static boolean error = true;	//1
	static boolean info = false;	//2
	static boolean log = true;		//3
	
	/**
	 * Print a debug message with debug-type.
	 * @param message - message to print to log
	 * @param type - 1: error, 2: info, 3: log
	 */
	public static void println(String message, int type){
		//ERROR
		if (type == 1 && error){
			System.out.println(DateTime.getLogDate() + " ERROR - " + message);
		}
		//INFO
		else if (type == 2 && info){
			System.out.println(DateTime.getLogDate() + " INFO - " + message);
		}
		//LOG
		else if (type == 3 && log){
			System.out.println(DateTime.getLogDate() + " LOG - " + message);
		}
	}
	
	/**
	 * Print the desired number of stackTraces (or less).
	 */
	public static void printStackTrace(Throwable ex, int numTraces){
		StackTraceElement[] ste = ex.getStackTrace(); 
		if (ste != null){
			for (int i=0; i<Math.min(5, ste.length); i++){
				Debugger.println("TRACE: " + ex.getStackTrace()[i], 1);
			}
		}
	}
	
	/**
	 * Get a tic.
	 */
	public static long tic(){
		return System.currentTimeMillis();
	}
	/**
	 * Get a toc from a tic :-)
	 */
	public static long toc(long tic){
		long toc = System.currentTimeMillis()-tic;
		return toc;
	}
	
	/**
	 * Sleep for a while.
	 * @param duration - time in ms
	 */
	public static void sleep(long duration){
		try{ Thread.sleep(duration); }catch(InterruptedException e){e.printStackTrace();}
	}
	
	/**
	 * Print out a Map (String, any).
	 */
	public static void printMap(Map<String, ?> hm){
		for (Entry<String, ?> entry : hm.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}
	/**
	 * Print out an ArrayList (String).
	 */
	public static void printList(List<?> list){
		for (Object e : list){
			System.out.println("List element: " + e);
		}
	}
	/**
	 * Print array.
	 */
	public static void printArray(Object[] objects){
		System.out.println(Arrays.toString(objects));
	}

}
