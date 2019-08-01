package net.b07z.sepia.server.core.assistant;

/**
 * Collection of commands and parameters (see comments) to use as constants and avoid typos etc.
 * @author Florian Quirin
 *
 */
public class CMD {
																//TODO: update this list
	//static list of all commands ... to use in interpreters or whatever
	final public static String ABORT = "abort";						//parameters:	-
	final public static String ALARM = "alarm";						//parameters:	action, time, reply
	final public static String ASK_CLIENT = "ask_client";				//parameters:	-
	final public static String BANKING = "banking";					//parameters:	action (<pay>, <show>, <send>), receiver, number, currency
	final public static String CAR_WELCOME_UPDATE = "car_welcome_up";	//parameters:	-
	final public static String CHAT = "chat";							//parameters:	apology, complain, compliment, question, ...
	final public static String CLIENT_CONTROLS = "client_controls";			//parameters:	client_fun
	final public static String PLATFORM_CONTROLS = "platform_controls";		//parameters:	android_fun, ios_fun, browser_fun, device_fun
	final public static String CONTEXT = "context";					//parameters:	- all of the previous command ... -
	final public static String CONTROL = "control";					//parameters:	type (lights, heater, ...), action (on, off, set, inc. ... complex?), info (room etc.), number (for temp. etc.)
	final public static String COUNT = "count";						//parameters:	number, memory (saves numbers said)
	final public static String CS_TELCOM = "cs_telcom";				//parameters:	action (<get_offer>, <get_help>), info (telcom object, like mobile), type (any type like prepaid)
	final public static String CUSTOM = "custom";						//parameters:	cs_route, cs_cmd, arbitrary...
	final public static String DASHBOARD = "dashboard";				//parameters:	type, info
	final public static String DEMO = "demo";							//parameters:	-
	final public static String DICT_TRANSLATE = "dict_translate";		//parameters:	search, language
	final public static String DIRECTIONS = "directions";				//parameters:	location_start, location_end, type, time
	final public static String EVENTS_PERSONAL = "events_personal";	//parameters:	-
	final public static String FASHION = "fashion"; 					//parameters:	fashion_item, size, color, gender, material
	final public static String FEEDBACK_NPS = "feedback_nps";			//parameters: 	yesNo
	final public static String FLIGHTS = "flights";					//parameters:	location_start, location_end, time
	final public static String FOOD = "food";							//parameters: 	type (pizza), info (with tomato and cheese), address, time(?)
	final public static String HOTELS = "hotels";						//parameters:	place, time
	final public static String INSURANCE = "insurance";				//parameters:	action (<get_offer>, <get_help>), info (insurance object), type (any type like vollkasko)
	final public static String KNOWLEDGEBASE = "knowledgebase";		//parameters:	search, type
	final public static String LANGUAGE_SWITCH = "language_switch";	//parameters:	language
	final public static String LISTS = "lists";						//parameters:	type, action, info
	final public static String LOCATION = "location";					//parameters:	search, poi (like italian restaurant, supermarket etc. ...)
	final public static String MATCH = "match";						//parameters: 	info
	final public static String MESH_NODE_PLUGIN = "mesh_node_plugin";		//parameters: 	node_url, node_plugin_name, node_plugin_data, reply_success, reply_fail 
	final public static String MOBILITY = "mobility";					//parameters:	location_start, location_end, type, time
	final public static String MOVIES = "movies";						//parameters:	search, type (genre), info (actor,director)
	final public static String MUSIC = "music";						//parameters:	search, type (genre), info (artist?)
	final public static String MUSIC_RADIO = "music_radio";			//parameters:	search (station name or genre)
	final public static String MY_FAVORITE = "my_favorite";			//parameters:	type, info
	final public static String NEWS = "news";							//parameters:	type
	final public static String NO_RESULT = "no_result";				//parameters:	text
	final public static String OPEN_LINK = "open_link";				//parameters:	url, parameter_set, question_set, answer_set, link_info, link_ico
	final public static String REMINDER = "reminder";					//parameters:	type, action, info
	final public static String SEARCH_VEHICLE = "search_vehicle";		//parameters:	type, search, am_money, am_power, am_distance, color, age_y
	final public static String SEARCH_RETAIL = "search_retail";		//parameters:	type, search, am_money, color
	final public static String SMARTDEVICE = "smartdevice";			//parameters:	device (lights, heater, ...), action (on, off, set, inc. ... complex?), room (room etc.), number (for temp. etc.)
	final public static String TICKETS = "tickets";					//parameters: 	search, type (cinema, event, sport ...)
	final public static String TIMER = "timer";						//parameters:	action, time, reply
	final public static String TV_PROGRAM = "tv_program";				//parameters:	channel, time
	final public static String REPEAT = "repeat";						//parameters:	-
	final public static String REPEAT_ME = "repeat_me";					//parameters:	-
	final public static String RESULT_REDIRECT = "result_redirect";		//parameters:	-
	final public static String SENTENCE_CONNECT = "sentence_connect";	//parameters:	-
	final public static String WEATHER = "weather";					//parameters:	place, time
	final public static String WEB_SEARCH = "websearch";				//parameters:	websearch_q

}
