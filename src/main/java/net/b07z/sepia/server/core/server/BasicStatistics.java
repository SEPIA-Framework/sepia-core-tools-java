package net.b07z.sepia.server.core.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Track all sorts of statistics like API calls, etc.
 * Use this as basis and extend it in your server.
 * 
 * @author Florian Quirin
 *
 */
public class BasicStatistics {
	
	public static String getBasicInfo(){
		String msg = "";
		msg += "Other APIs:<br>";
		for (Map.Entry<String, AtomicInteger> entry : otherApiHits.entrySet()) {
			int hit = entry.getValue().get();
			String name = entry.getKey();
			msg += ("- " + name + ": " + hit + " hits<br>");
			if (otherApiTime.containsKey(name)){
				msg += ("- " + name + ": " + ((double)otherApiTime.get(name).get())/((double)hit) + " ms per call<br>");
			}
		}
		msg += "<br>";
		msg += "External APIs:<br>";
		for (Map.Entry<String, AtomicInteger> entry : externalApiHits.entrySet()) {
			int hit = entry.getValue().get();
			String name = entry.getKey();
			msg += ("- " + name + ": " + hit + " hits<br>");
			if (externalApiTime.containsKey(name)){
				msg += ("- " + name + ": " + ((double)externalApiTime.get(name).get())/((double)hit) + " ms per call<br>");
			}
		}
		msg += "<br>";
		return msg;
	}
	
	//other calls
	private static HashMap<String, AtomicInteger> otherApiHits = new HashMap<>();
	public static void addOtherApiHit(String apiName){
		if (otherApiHits.containsKey(apiName)){
			otherApiHits.get(apiName).incrementAndGet();
		}else{
			otherApiHits.put(apiName, new AtomicInteger());
			otherApiHits.get(apiName).incrementAndGet();
		}
	}
	private static HashMap<String, AtomicLong> otherApiTime = new HashMap<>();
	public static void addOtherApiTime(String apiName, long tic){
		long time = System.currentTimeMillis()-tic;
		if (otherApiTime.containsKey(apiName)){
			otherApiTime.get(apiName).addAndGet(time);
		}else{
			otherApiTime.put(apiName, new AtomicLong());
			otherApiTime.get(apiName).addAndGet(time);
		}
	}
		
	//external APIs
	private static HashMap<String, AtomicInteger> externalApiHits = new HashMap<>();
	public static void addExternalApiHit(String apiName){
		if (externalApiHits.containsKey(apiName)){
			externalApiHits.get(apiName).incrementAndGet();
		}else{
			externalApiHits.put(apiName, new AtomicInteger());
			externalApiHits.get(apiName).incrementAndGet();
		}
	}
	private static HashMap<String, AtomicLong> externalApiTime = new HashMap<>();
	public static void addExternalApiTime(String apiName, long tic){
		long time = System.currentTimeMillis()-tic;
		if (externalApiTime.containsKey(apiName)){
			externalApiTime.get(apiName).addAndGet(time);
		}else{
			externalApiTime.put(apiName, new AtomicLong());
			externalApiTime.get(apiName).addAndGet(time);
		}
	}
}
