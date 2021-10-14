package net.b07z.sepia.server.core.assistant;

/**
 * This class collects all default actions. Use the statics in your API to make sure the client checks for it.
 * 
 * @author Florian Quirin
 *
 */
public class ACTIONS {
	
	//static list of all actions that can be send to a client
	final public static String OPEN_URL = "open_url";							//tell client to open an URL in external browser, parameters: url
	final public static String BUTTON_URL = "button_url";						//tell client to show a button with an URL that leads to external browser, parameters: url, title
	final public static String OPEN_IN_APP_BROWSER = "open_in_app_browser";		//tell client to open an URL in internal browser, parameters: url
	final public static String BUTTON_IN_APP_BROWSER = "button_in_app_browser";	//tell client to show a button with an URL that opens in internal browser, parameters: url, title
	final public static String OPEN_CMD = "open_cmd";					//CURRENTLY NOT USED / tell client to open a command stored as summary, parameters: cmd
	final public static String QUEUE_CMD = "queue_cmd";					//tell client to queue a command stored as summary, parameters: cmd
	final public static String SCHEDULE_CMD = "schedule_cmd";			//tell client to trigger a command stored as summary, parameters: cmd, targetTimeUnix
	final public static String BUTTON_CMD = "button_cmd";				//tell client to show a button to open a command stored as summary, parameters: cmd, title, info (text, direct_cmd), visibility (all, inputHidden, noConversation),
	final public static String SHOW_ABORT_OPTION = "show_abort";		//tell client to show an abort option
	final public static String SHOW_HTML_RESULT = "show_html_result";			//tell client to show a block of simple, plain HTML data in a result window, parameters: data
	final public static String SHOW_HTML_SANDBOX = "show_html_sandbox";			//tell client to show a block of complex HTML data in a result window BUT in a sandbox (e.g. iframe), parameters: data
	final public static String BUTTON_CUSTOM_FUN = "button_custom_fun";		//tell client to show a custom button that executes a function, parameters: title, fun
	final public static String CLIENT_CONTROL_FUN = "client_control_fun";	//tell client to run a control function (pre-defined in client), parameters: fun, controlData, delayUntilIdle
	final public static String TRIGGER_CUSTOM_EVENT = "trigger_custom_event";	//tell client to trigger an event (with prefix 'sepia-action-custom-event-...'), parameters: name (event), data
	final public static String BUTTON_CUSTOM_EVENT = "button_custom_event";		//tell client to show a custom button that triggers an event (with prefix 'sepia-action-custom-event-...'), parameters: title (button), name (event), data
	final public static String SCHEDULE_MSG = "schedule_msg";			//tell client to schedule a message (like a push message but locally triggered)
	final public static String OPEN_HELP = "open_help";				//tell client to open the help window
	final public static String BUTTON_HELP = "button_help";			//tell client to show a help button
	final public static String OPEN_SETTINGS = "open_settings";		//USED FOR HELP MSG (use CLIENT_CONTROL for other) - tell client to note user about settings/client menu options, parameters: section (addresses, favorites, contacts, common, user_home, user_work, user_name, ...), info (?)
	final public static String OPEN_TEACH_UI = "open_teach_ui";			//tell client to open the teach UI, parameters: info (input, suggestion, ...)
	final public static String BUTTON_TEACH_UI = "button_teach_ui";		//tell client to show the teach UI button, parameters: info (input, suggestion, ...)
	final public static String OPEN_FRAMES_VIEW = "open_frames_view";		//tell client to open a frames view, parameters: info (frame config)
	final public static String CLOSE_FRAMES_VIEW = "close_frames_view";		//tell client to close a frames view if open, parameters: -
	final public static String FRAMES_VIEW_ACTION = "frames_view_action";	//tell client to send the custom action to the open frames view 'actionHandler', parameters: info [custom action] 
	final public static String BUTTON_FRAMES_VIEW = "button_frames_view";	//tell client to show a frames view button, parameters: title, info (frame config)
	final public static String PLAY_AUDIO_URL = "play_audio_url";			//tell client to play a single audio file from url, parameters: audio_url
	final public static String PLAY_AUDIO_STREAM = "play_audio_stream";	//tell client to play a continuous audio stream, parameters: audio_url, audio_title, audio_img, audio_type
	final public static String STOP_AUDIO_STREAM = "stop_audio_stream";	//tell client to stop the running audio stream
	final public static String TIMER = "timer";					//tell client to set a timer, parameters: tbd
	final public static String ALARM = "alarm";					//tell client to set an alarm, parameters: tbd
	final public static String REMINDER = "reminder";			//tell client to set an reminder, parameters: tbd
	final public static String APPOINTMENT = "appointment";		//tell client to set an appointment in the calendar, parameters: tbd
	final public static String SWITCH_LANGUAGE = "switch_language";		//tell client to switch language, parameters: language_code
	final public static String SWITCH_STT_ENGINE = "switch_stt_engine";	//tell client to switch STT engine, parameters: engine (e.g. 'native' or 'socket'), url (e.g. WebSocket URL to server)
	
	//special actions
	final public static String MOD_EVENTS_START = "events_start";						//tell client that the results of the events endpoint start here
	final public static String MOD_FIRST_VISIT_INFO_START = "first_visit_info_start";	//tell client that the actions of the first-visit-info start here

	//possible next actions:
	//public static String STREAMING_REQUEST = "streaming_request";		//tell the client that he has to expect an answer that gets streamed
	//public static String GET_HTML_META_INFO = "get_html_meta_info";	//tell client to open an url, get meta info when the page is loaded and say it, parameters: meta_tags, add_answer
	
	//ACTION OPTIONS
	final public static String OPTION_SKIP_TTS = "skipTTS";
	final public static String OPTION_SKIP_TEXT = "skipText";
	final public static String OPTION_SKIP_ACTIONS = "skipActions";
	final public static String OPTION_SKIP_NONE_BUTTON_ACTIONS = "skipNoneButtonActions";	//skip all actions that are not buttons
	final public static String OPTION_LOAD_DATA_ONLY = "loadOnlyData";		//skips everything but loading data in background
	final public static String OPTION_TARGET_VIEW = "targetView";			//chat, myView, bigResults
	final public static String OPTION_SHOW_VIEW = "showView";				//show target?
}
