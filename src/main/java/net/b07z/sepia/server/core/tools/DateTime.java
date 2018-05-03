package net.b07z.sepia.server.core.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Tools to extract dates and times and to create them.
 * 
 * @author Florian Quirin
 *
 */
public class DateTime {
	
	/**
	 * Get default date string for logger. 
	 */
	public static String getLogDate(){
		return getFormattedDate("yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * Get locale date string with custom format. 
	 */
	public static String getFormattedDate(String format){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	/**
	 * Get a custom formatted string with the current GMT Time/Date. GMT is the default in this framework.
	 * @param format - desired format like "yyyy/dd/MM" or "dd.MM.yyyy' - 'HH:mm:ss' - GMT'"
	 * @return String in the given format
	 */
	public static String getGMT(String format){
		Date date = new Date();
		return getGMT(date, format);
	}
	/**
	 * Get a custom formatted string with the GMT Time/Date of the given "date".
	 * @param date - Date object allocated at some point of time
	 * @param format - desired format like "HH:mm:ss" or "dd.MM.yyyy"
	 * @return String in given format at given date
	 */
	public static String getGMT(Date date, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(date);
	}
	/**
	 * Get a custom formatted string of the given UNIX time at a given timezone.
	 * @param unixTS - time-stamp of UNIX time
	 * @param format - desired format like "HH:mm:ss" or "dd.MM.yyyy"
	 * @param timeZone - String representation of time zone like "America/Los_Angeles"
	 */
	public static String getDateAtTimeZone(long unixTS, String format, String timeZone){
		Date date = new Date(unixTS);
		TimeZone tz = TimeZone.getTimeZone(timeZone);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(tz);
		return sdf.format(date);
	}

}
