package net.b07z.sepia.server.core.tools;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
	 * Print out a HashMap (String, String).
	 */
	public static void printMap_SS(Map<String, String> hm){
		for (Map.Entry<String, String> entry : hm.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}
	/**
	 * Print out a HashMap (String, Long).
	 */
	public static void printMap_SL(Map<String, Long> hm){
		for (Map.Entry<String, Long> entry : hm.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}
	/**
	 * Print out a HashMap (String, Object).
	 */
	public static void printMap_SO(Map<String, Object> hm){
		for (Map.Entry<String, Object> entry : hm.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}
	/**
	 * Print out an ArrayList (String).
	 */
	public static void printList_S(List<String> list){
		for (String e : list){
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
