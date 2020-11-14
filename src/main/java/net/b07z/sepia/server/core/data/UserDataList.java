package net.b07z.sepia.server.core.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.tools.Is;
import net.b07z.sepia.server.core.tools.JSON;
import net.b07z.sepia.server.core.tools.RandomGen;

/**
 * A general list for all kinds of stuff like shopping, to-do, alarms, etc. with some common structure.
 * 
 * @author Florian Quirin
 *
 */
public class UserDataList {
	//database types to organize commands
	public static final String LISTS_TYPE = "lists";
	
	public enum Section{
		all,
		productivity,
		timeEvents,
		personalInfo
	}
	public enum IndexType{
		shopping,
		todo,
		reminders,
		appointments,
		alarms, 			//includes timers
		newsFavorites,
		unknown
	}
	public enum EleType{
		checkable,
		timer,
		alarm
	}
	public enum EleState{
		open,
		inProgress,
		done
	}
	public static boolean indexTypeContains(String test){
	    for (IndexType c : IndexType.values()) {
	        if (c.name().equals(test)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	//List commons
	public String _id;			//unique ID generated for this list - restructured, is elasticSearch ID now
	public String user;			//user of this list
	public Section section;		//a topic that groups lists and prevents cross-talk when searching only for title
	public String indexType;	//things like shopping, to-do, reminders, alarms, etc. ... used as DB type as well
	public String title;		//name of the list in the sub-category like 'supermarket' for type 'shopping'
	public String titleHtml;	//HTML version of title if you need to make look nice
	public String icon; 		//URL to an icon image that can be used for the list
	public String desc; 		//a short description of this list
	
	public String type;			//type of list classifying how the list is structured
	public String group;		//a group that can be used to cluster the data (somehow)
	public JSONObject moreInfo;	//anything else (that you don't want to write on top-level)
	
	public JSONArray data;		//list entries, usually each is a JSONObject
	
	public long lastEdit;		//when was the list last modified? 
	
	/**
	 * Import list from JSON.
	 */
	public UserDataList(String listJson){
		importJSON(listJson);
	}
	/**
	 * Import list from JSON with known id.
	 */
	public UserDataList(String listJson, String id){
		importJSON(listJson);
		this._id = id;
	}
	/**
	 * Import list from JSON.
	 */
	public UserDataList(JSONObject listJson){
		importJSON(listJson);
	}
	/**
	 * Import list from JSON with known id.
	 */
	public UserDataList(JSONObject listJson, String id){
		importJSON(listJson);
		this._id = id;
	}
	/**
	 * Create most basic list. 'title' can be empty to get default title.
	 */
	public UserDataList(String user, Section section, String indexType, String title, JSONArray data){
		this.user = user;
		this.section = section;
		this.indexType = indexType;
		this.title = title;
		this.data = data;
	}
	
	/**
	 * Set _id if its known.
	 */
	public void setId(String id){
		this._id = id;
	}
	/**
	 * Get _id.
	 */
	public String getId(){
		return this._id;
	}
	
	/**
	 * Export this list as JSON
	 */
	public JSONObject getJSON(){
		JSONObject jo = new JSONObject();
		JSON.addIfNotNullOrEmpty(jo, "_id", _id);
		JSON.add(jo, "user", user);
		JSON.add(jo, "section", section.name());
		JSON.add(jo, "indexType", indexType);
		JSON.add(jo, "title", title);
		JSON.addIfNotNullOrEmpty(jo, "titleHtml", titleHtml);
		JSON.addIfNotNullOrEmpty(jo, "icon", icon);
		JSON.addIfNotNullOrEmpty(jo, "desc", desc);
		JSON.addIfNotNullOrEmpty(jo, "type", type);
		JSON.addIfNotNullOrEmpty(jo, "group", group);
		JSON.addIfNotNull(jo, "moreInfo", moreInfo);
		JSON.add(jo, "data", data);
		JSON.add(jo, "lastEdit", lastEdit);
		return jo;
	}
	
	/**
	 * Take a (java) List full of UserDataLists and make an JSONArray out of it.
	 */
	public static JSONArray convertManyListsToJSONArray(List<UserDataList> manyLists){
		JSONArray ja = new JSONArray();
		for (UserDataList uda : manyLists){
			JSON.add(ja, uda.getJSON());
		}
		return ja;
	}
	/**
	 * Take a JSONArray full of UserDataLists and make a (java) List out of it.
	 */
	public static List<UserDataList> convertJSONArrayToLists(JSONArray jsonArray){
		List<UserDataList> manyLists = new ArrayList<>();
		for (Object o : jsonArray){
			manyLists.add(new UserDataList((JSONObject) o));
		}
		return manyLists;
	}
	
	/**
	 * Import list from JSONObject.
	 */
	public void importJSON(JSONObject list){
		_id = (String) list.get("_id");
		user = (String) list.get("user");
		section = Section.valueOf(((String) list.get("section")));
		indexType = (String) list.get("indexType");
		title = (String) list.get("title");
		titleHtml = (String) list.get("titleHtml");
		icon = (String) list.get("icon");
		desc = (String) list.get("desc");
		type = (String) list.get("type");
		group = (String) list.get("group");
		moreInfo = (JSONObject) list.get("moreInfo");
		data = (JSONArray) list.get("data");
		lastEdit = JSON.getLongOrDefault(list, "lastEdit", 0);
	}
	/**
	 * Import list from string (in JSON format).
	 */
	public void importJSON(String list){
		JSONObject listJ = JSON.parseStringOrFail(list);
		importJSON(listJ);
	}
	
	//--- ENTRY BUILDER ---
	
	/**
	 * Create an entry for a user-data list of element type 'alarm'.
	 * @param targetTimeUnix - Target time as UNIX timestamp (milliseconds)
	 * @param day - A "speakable" string for the day
	 * @param time - Time as given by NLU parameter (default format) 
	 * @param date - A "speakable" string for the date
	 * @param repeat - Repeat alarm? Client currently supports: "onetime"
	 * @param name - Any name describing the alarm (e.g. time, date or something like "Wake-Up Monday") 
	 * @param eventId - An ID to identify this alarm. Has to be unique for a given user. Use 'null' to auto-generate. 
	 * @param lastChange - Timestamp of last change
	 * @param activated - Has the alarm been activated already by any client?
	 * @return
	 */
	public static JSONObject createEntryAlarm(long targetTimeUnix, 
			String day, String time, String date, String repeat, String name, 
			String eventId, long lastChange, boolean activated){
		
		if (Is.nullOrEmpty(eventId)){
			eventId = getWeakRandomId("alarm");
		}
		JSONObject entry = JSON.make(
				"eleType", UserDataList.EleType.alarm.name(),	//fix
				"targetTimeUnix", targetTimeUnix,
				"day", day,
				"time", time,
				"date", date,
				"repeat", repeat
		);
		JSON.put(entry, "name", name.trim());
		JSON.put(entry, "eventId", eventId);
		JSON.put(entry, "lastChange", lastChange);
		JSON.put(entry, "activated", activated);
		
		return entry;
	}
	/**
	 * Create an entry for a user-data list of element type 'timer'.
	 * @param targetTimeUnix - Target time as UNIX timestamp (milliseconds)
	 * @param name - Any name describing the timer (e.g. time, date or something like "Pizza in oven") 
	 * @param eventId - An ID to identify this timer. Has to be unique for a given user. Use 'null' to auto-generate. 
	 * @param lastChange - Timestamp of last change
	 * @param activated - Has the timer been activated already by any client?
	 * @return
	 */
	public static JSONObject createEntryTimer(long targetTimeUnix, String name, 
			String eventId, long lastChange, boolean activated){
		
		if (Is.nullOrEmpty(eventId)){
			eventId = getWeakRandomId("timer");
		}
		JSONObject entry = JSON.make(
				"eleType", UserDataList.EleType.timer.name(),	//fix
				"targetTimeUnix", targetTimeUnix,
				"name", name.trim(),
				"eventId", eventId,
				"lastChange", lastChange,
				"activated", activated
		);
		
		return entry;
	}
	/**
	 * Create an entry for a user-data list of element type 'checkable'.
	 * @param name - Any name describing the entry, e.g. "Butter" for a shopping list etc.
	 * @param lastChange - Timestamp of last change 
	 * @param hasMoreThanTwoStates - Is this a simple entry with just 2 states (checked/unchecked) or does it have more? (e.g. open, inProgress, done)
	 * @return
	 */
	public static JSONObject createEntryCheckable(String name, long lastChange, boolean hasMoreThanTwoStates){
		JSONObject entry = JSON.make(
			"eleType", UserDataList.EleType.checkable.name()
		);
		String state = null;
		if (hasMoreThanTwoStates){
			state = UserDataList.EleState.open.name();		//e.g. to-do lists have a state (open, inProgress, ...) in addition to 'checked'
		}
		//TODO: add ID?
		JSON.put(entry, "name", name.trim());
		JSON.put(entry, "lastChange", lastChange);
		JSON.put(entry, "checked", Boolean.FALSE);			//initialized with FALSE and 'open'
		if (state != null){
			JSON.put(entry, "state", state);
		}
		//JSON.put(item, "metaData", new JSONObject());
		
		return entry;
	}
	
	/**
	 * A a relatively weak (but sufficient if the scope is OK) random ID with custom prefix.
	 * @param prefix - e.g. "timer" or "alarm"
	 * @return
	 */
	public static String getWeakRandomId(String prefix){
		String eventIdSuffix = System.currentTimeMillis() + "-" + RandomGen.getInt(100, 999);
		return (prefix + "-" + eventIdSuffix);
	}
}
