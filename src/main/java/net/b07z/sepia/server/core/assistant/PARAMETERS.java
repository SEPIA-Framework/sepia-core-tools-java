package net.b07z.sepia.server.core.assistant;

/**
 * Collection of parameters used in commands. If you stick to these for your API you get different advantages like auto tweaking on response.
 * 
 * @author Florian Quirin
 *
 */
public class PARAMETERS {
	
	//TODO:	NOTE: PARAMETERS CAN NEVER INCLUDE ";;" AND NEVER START WITH "cs_..." !!!
	//		PARAMETERS CAN HEAVILY INFLUENCE THE RESPONSE HANDLER
	//		IF YOU CHANGE A NAME YOU HAVE TO CHECK ALL CLIENTS, ACTIONS AND SERVICES IF ANY IS USING OLD NAME STILL ^^
	
	//--------- unsorted list / not fully implemented or deprecated ----------
	
	final public static String SEARCH = "search";			//general search, web search 		- included in response tweaking
	final public static String TYPE = "type";				//a type of something depending on context
	final public static String COLLECTION = "collection";	//a collection or database to query, e.g. "messe_muenchen" 
	final public static String INFO = "info";				//additional info, depending on the context
	final public static String CMDS = "cmds";				//one or more command(s)
	final public static String KEYS = "keys";				//one or more keys to predefined parameter(s)
	final public static String VALUES = "values";			//one or more values to predefined parameter(s) 
	final public static String TIME_END = "time_end";		//time finish, e.g. when you book a travel ... from ... to - TODO: always use default date and included in response tweaking
	final public static String AGE_YEARS = "age_y";		//age in years like, 5 years ago, not older than 1999
	final public static String NUMBER = "number";			//any number, decimal or integer ...
	final public static String AMOUNT_POWER = "am_power";		//number for power, e.g. 75 PS, 55 KW
	final public static String AMOUNT_ENERGY = "am_energy";	//number for energy, e.g. 5 KJ, 10 J
	final public static String AMOUNT_MONEY = "am_money";		//number for money, e.g. 50€, 10 Dollars
	final public static String AMOUNT_WEIGHT = "am_weight";		//number for weight, e.g. 50g, 10 tons
	final public static String AMOUNT_DISTANCE = "am_distance";	//number for distance, e.g. 500m, 10 km
	final public static String RECEIVER = "receiver";		//receiver of messages, banking actions etc. ... this is somewhat special ... see e.g. Banking_Default plug-in 
	final public static String CURRENCY = "currency";		//currency e.g. used for banking etc. ...
	final public static String CHANNEL = "channel";			//e.g. TV channel
	final public static String URL = "url"; 				//a link for different purposes
	final public static String FUNCTION = "function"; 		//a function for calculations etc.
	final public static String SENTENCES = "sentences"; 	//one or more sentences returned as parameter
	//FLIGHTS
	final public static String ADULTS = "adults";					//number of adult persons
	final public static String FLIGHT_CLASS = "flight_class";		//economy? first class?
	final public static String FLIGHT_TYPE = "flight_type";			//oneway? roundtrip?
	//ALARMS
	final public static String ALARM_NAME = "alarm_name";			//assumed name of an alarm
	
	//------------ common -------------
	
	final public static String YES_NO = "yes_no";		//<yes> or <no>
	final public static String LANGUAGE = "language";	//language e.g. for translations
	final public static String ACTION = "action";		//an action depending on context
	final public static String TIME = "time";			//times like dates, days, etc. - TODO: always uses default date and is included in response tweaking
	final public static String CLOCK = "clock";			//child parameter of TIME that only takes the date_time, useful if you need to check for date and time separately
	final public static String GENDER = "gender"; 		//male, female, unisex
	final public static String COLOR = "color";			//colors ...
	
	//------------ service specific -------------
	
	//LOCATION / PLACES / NAVIGATION
	final public static String PLACE = "place";			//place for weather, locations etc. - included in response tweaking
	final public static String POI = "poi";				//point of interest
	final public static String LOCATION_START = "location_start";		//place to start a navigation etc. - included in response tweaking
	final public static String LOCATION_WAYPOINT = "waypoint";		//place to stop during navigation
	final public static String LOCATION_IS_LOCAL_WP = "is_local_wp"; 	//is the waypoint local and should be close to start?
	final public static String LOCATION_END = "location_end";			//place to finish a navigation etc. - included in response tweaking
	final public static String TRAVEL_TYPE = "travel_type";					//transit, driving, etc.
	final public static String TRAVEL_REQUEST_INFO = "travel_request_info";	//duration, navigation, map, overview
	
	//NEWS - SPORTS
	final public static String NEWS_SECTION = "news_section";		//sections like main, sports, politics, economy, science, ...
	final public static String NEWS_TYPE = "news_type";			//overview, topic, results, ... e.g. used to distinguish between "show me some news" and "show me soccer results"
	final public static String NEWS_SEARCH = "news_search";		//search phrase to a specific topic like "earthquake in Timbuktu"
	final public static String SPORTS_TEAM = "sports_team";		//soccer teams, etc. ...
	final public static String SPORTS_LEAGUE = "sports_league";	//soccer leagues, etc. ...
	
	//ALARMS
	final public static String ALARM_TYPE = "alarm_type";		//alarm clock, reminder, timer, ...
	
	//SHOPPING
	final public static String FASHION_ITEM = "fashion_item";		//things like shoes, pants, shirts, etc. ...
	final public static String FASHION_SIZE = "fashion_size";		//things like 42, XL, xxs, 5, ...
	final public static String FASHION_BRAND = "fashion_brand";	//adidas, puma, nike, ...
	
	//FOOD DELIVERY
	final public static String FOOD_ITEM = "food_item";		//pizza, pasta, döner, etc. ...
	final public static String FOOD_CLASS = "food_class";		//italienisch, vegetarisch, vegan
	
	//RADIO - MUSIC
	final public static String RADIO_STATION = "radio_station";	//name of a radio station
	final public static String SONG = "song";						//a song
	final public static String MUSIC_ARTIST = "artist";			//an artist (music or something else)
	final public static String MUSIC_GENRE = "genre";				//music genre (other genres too?)
	
	//SMART DEVICES
	final public static String SMART_DEVICE = "device";			//any smart device
	final public static String SMART_LOCATION = "smart_place";	//a room or place in the smart environment
	
	//SEARCH
	final public static String WEBSEARCH_REQUEST = "websearch_q";			//the term to look for
	final public static String WEBSEARCH_ENGINE = "websearch_engine";		//the engine to use
	final public static String SEARCH_SECTION = "search_section";			//things like pictures, videos, movies, ...
	
	//LISTS
	final public static String LIST_ITEM = "list_item";			//item on a list
	final public static String LIST_TYPE = "list_type";			//type of list (e.g. shopping, todo, ...)
	final public static String LIST_SUBTYPE = "list_subtype";	//sub-type aka name of list (e.g. my great ..., my macys ...)
	
	//------------ control parameters -------------
	
	//CHATS and QUESTIONS
	final public static String REPLY = "reply";					//a reply the assistant should give submitted as parameter from client
	final public static String ANY_ANSWER = "any_answer";		//if you don't have a particular parameter that is missing but you ask a previously undefined question you can store the answer here. Maybe add dialog_state to it.
	
	//CUSTOM SERVICES (do we still need this?)
	final public static String CS_ROUTE = "cs_route"; 			//route to custom service, e.g. a server address or simply "local"
	final public static String CS_COMMAND = "cs_cmd";			//custom service command, so the target server can map it
	
	//CONTROL PARAMETERS
	final public static String SELECTION = "selection";		//control parameter: when a command requires the user to choose one of the results ...(use numbers, maybe names too)
	final public static String ANSWER_STAGE = "ans_stage";	//control parameter: ...if you want to keep track of dialog_stage in command or want to have the option to skip stages
	final public static String MEMORY = "memory";				//control parameter: ...if you want to keep track of things that have been said already like a command history
	final public static String FINAL = "final";					//control parameter: collect parameter names here that are checked, validated and final. Use a list like "[location_start,location_end,time]"
	final public static String CONFIRMATION = "confirmation";	//control parameter: when a critical command requires a confirmation ...(use: ok, cancel)
	final public static String DYNAMIC = "dynamic";				//control parameter: collect parameter names here that are dynamically set during service module to include them in interview build scripts. Use a list like in FINAL
	
	//Exotic or unique stuff
	final public static String REPEAT_THIS = "repeat_this";   	//what the assistant shall repeat

}
