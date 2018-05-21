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
	final public static String SHOW_HTML_RESULT = "show_html_result";			//tell client to show a block of HTML data in a result window, parameters: data
	final public static String OPEN_CMD = "open_cmd";					//tell client to open a command stored as summary, parameters: cmd
	final public static String QUEUE_CMD = "queue_cmd";					//tell client to queue a command stored as summary, parameters: cmd
	final public static String SCHEDULE_CMD = "schedule_cmd";			//tell client to repeat a command stored as summary, parameters: cmd, executeIn, waitForIdle
	final public static String BUTTON_CMD = "button_cmd";				//tell client to show a button to open a command stored as summary, parameters: cmd, title, type (text, direct_cmd), visibility (all, inputHidden, noConversation),
	final public static String EVENTS_START = "events_start";			//tell client that the results of the events endpoint start here, parameters: info (text, quietText, divider), text
	final public static String SCHEDULE_MSG = "schedule_msg";			//tell client to schedule a message (like a push message but locally triggered)
	final public static String OPEN_INFO = "open_info";				//tell client to open the info window
	final public static String OPEN_CARDS = "open_cards";				//tell client to open the cards search result view
	final public static String OPEN_HELP = "open_help";				//tell client to open the help window
	final public static String BUTTON_HELP = "button_help";				//tell client to show a help button
	final public static String OPEN_DASHBOARD = "open_dashboard";		//tell client to open the user dashboard, parameters: type (common, settings, user_home, user_work, user_name, ...)
	final public static String OPEN_TEACH_UI = "open_teach_ui";		//tell client to open the teach UI, parameters: type (input, suggestion, ...)
	final public static String BUTTON_TEACH_UI = "button_teach_ui";	//tell client to show the teach UI button, parameters: type (input, suggestion, ...)
	final public static String OPEN_LIST = "open_list";				//tell client to open a user specific list, param.: "listInfo" : {"indexType":...}
	final public static String EXTEND_REQUEST = "extend_request";		//tell client that this request will take longer and he should prepare for that
	final public static String SHOW_ABORT_OPTION = "show_abort";		//tell client to show an abort option
	//deprecated: final public static String SKIP_TTS = "skip_tts";			//tell client to prevent TTS sound output (best used together with PLAY_AUDIO)
	final public static String PLAY_AUDIO_URL = "play_audio_url";			//tell client to play a single audio file from url, parameters: audio_url
	final public static String PLAY_AUDIO_STREAM = "play_audio_stream";	//tell client to play a continuous audio stream, parameters: audio_url, audio_title, audio_img, audio_type
	final public static String STOP_AUDIO_STREAM = "stop_audio_stream";	//tell client to stop the running audio stream
	//final public static String FEEDBACK_ASK_NPS = "ask_feedback_nps";		//tell client to show the NPS question
	final public static String FEEDBACK_NPS = "open_feedback_nps";		//tell client to show the NPS screen
	final public static String TIMER = "timer";					//tell client to set a timer, parameters: tbd
	final public static String ALARM = "alarm";					//tell client to set an alarm, parameters: tbd
	final public static String REMINDER = "reminder";			//tell client to set an reminder, parameters: tbd
	final public static String APPOINTMENT = "appointment";		//tell client to set an appointment in the calendar, parameters: tbd

	//possible next actions:
	//public static String STREAMING_REQUEST = "streaming_request";		//tell the client that he has to expect an answer that gets streamed
	//public static String GET_HTML_META_INFO = "get_html_meta_info";	//tell client to open an url, get meta info when the page is loaded and say it, parameters: meta_tags, add_answer
	//public static String EXECUTE_CMD = "execute_cmd";					//tell client to execute an command, parameters: command
	
	//ACTION OPTIONS
	final public static String SKIP_TTS = "skipTTS";
	final public static String SKIP_TEXT = "skipText";
	final public static String SKIP_ACTIONS = "skipActions";
	final public static String SKIP_NONE_BUTTON_ACTIONS = "skipNoneButtonActions";	//skip all actions that are not buttons
	final public static String LOAD_DATA_ONLY = "loadOnlyData";		//skips everything but loading data in background
	final public static String TARGET_VIEW = "targetView";			//chat, myView, bigResults
	final public static String SHOW_VIEW = "showView";				//show target?
}