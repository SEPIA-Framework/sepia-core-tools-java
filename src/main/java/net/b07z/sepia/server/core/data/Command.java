package net.b07z.sepia.server.core.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.b07z.sepia.server.core.tools.JSON;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.simple.JSONObject;

/**
 * This class represents a command with its various incarnations and different languages.
 * 
 * @author Daniel Naber, Florian Quirin
 */
public class Command {

	//database types to organize commands
	public static final String COMMANDS_TYPE = "all";

	//variables to define the command
	private List<Sentence> sentences = new ArrayList<>();

	public String getCommand() {
		return command;
	}
	
	public Object getParameter(String name){
		return parameters.get(name);
	}
	
	/* The important info is in the sentences.cmd_summary element or in sentences.parameters if they are set
	public Object getParameters(){
		return parameters;
	}
	*/

	private String command;			//the actual command class, e.g. "weather" or "chat" (full list see assistant.CMD)
	private JSONObject parameters;

	public static Command importAnswerJSON(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
			return mapper.readValue(json, Command.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Command(){
	}

	public Command(String command){
		this.command = command;
	}
	
	public Command(String command, JSONObject parameters){
		this.command = command;
		this.parameters = parameters;
	}
	
	/**
	 * Add the command class as listed in assistant.CMD.
	 * @param command - class of command like "weather"
	 */
	public Command setCommand(String command) {
		this.command = command;
		return this;
	}
	
	/**
	 * Set all parameters for this command.
	 * @param parameters - a map with additional parameters as needed
	 * @return Command
	 */
	public Command setParameters(JSONObject parameters){
		this.parameters = parameters;
		return this;
	}
	
	/**
	 * Add a key-value pair to the "parameters" map.
	 * @param name - key String to add
	 * @param value - value Object
	 * @return Command
	 */
	public Command addParameter(String name, Object value) {
		if (this.parameters == null){
			this.parameters = new JSONObject();
		}
		JSON.put(this.parameters, name, value);
		return this;
	}
	
	//TODO: should we add "get"-methods or make the variables non-private?
	
	//------SENTENCE CLASS-------
	
	/**
	 * Sentence object of class Command, containing a language specific sentence with variable parameters triggering the command.  
	 * @author Daniel Naber
	 */
	@JsonPropertyOrder(alphabetic=true)  // stable order for tests
	public static class Sentence {
		// NOTE: keep in sync with commands-mapping.json! Only this way we can use Jackson to automatically 
		// serialize and deserialize from/to JSON.
		private Language language;
		private String text;
		private JSONObject params;
		private String cmdSummary;			//the command summary with parameters as it can be sent to Assistant-API
		private String user;
		private String source;
		private String environment;
		private String taggedText;
		private boolean isPublic;
		private boolean isLocal;
		private boolean isExplicit;
		private String userLocation;
		private List<String> replies;
		private boolean isMachineTranslated;
		private String translatedFrom;
		private String date;
		private Map<String, Object> data;		//any additional data we did not think of right now

		public Map<String, Object> getData() {
			return data;
		}

		public void setData(Map<String, Object> data) {
			this.data = data;
		}

		public String getTranslatedFrom() {
			return translatedFrom;
		}

		public void setTranslatedFrom(String translatedFrom) {
			this.translatedFrom = translatedFrom;
		}

		public boolean isMachineTranslated() {
			return isMachineTranslated;
		}

		public void setMachineTranslated(boolean machineTranslated) {
			isMachineTranslated = machineTranslated;
		}

		public List<String> getReplies() {
			return replies;
		}

		public void setReplies(List<String> replies) {
			this.replies = replies;
		}

		public String getUserLocation() {
			return userLocation;
		}

		public void setUserLocation(String userLocation) {
			this.userLocation = userLocation;
		}

		public boolean isExplicit() {
			return isExplicit;
		}

		public void setExplicit(boolean explicit) {
			isExplicit = explicit;
		}

		public boolean isLocal() {
			return isLocal;
		}

		public void setLocal(boolean local) {
			isLocal = local;
		}

		public boolean isPublic() {
			return isPublic;
		}

		public void setPublic(boolean aPublic) {
			isPublic = aPublic;
		}

		public String getTaggedText() {
			return taggedText;
		}

		public void setTaggedText(String taggedText) {
			this.taggedText = taggedText;
		}

		public String getEnvironment() {
			return environment;
		}

		public void setEnvironment(String environment) {
			this.environment = environment;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getCmdSummary() {
			return cmdSummary;
		}

		public void setCmdSummary(String cmdSummary) {
			this.cmdSummary = cmdSummary;
		}

		public JSONObject getParams() {
			return params;
		}

		public void setParams(JSONObject params) {
			this.params = params;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public Language getLanguage() {
			return language;
		}

		public void setLanguage(Language language) {
			this.language = language;
		}

		public Sentence() {
			// needed for ObjectMapper
		}

		/** Don't call this constructor directly, use SentenceBuilder! */
		Sentence(Language language, String text, String user, String source, String taggedText, JSONObject params, String cmdSummary,
						 String userLocation, boolean isMachineTranslated, String translatedFrom, boolean isPublic, boolean isLocal, boolean isExplicit, String environment, List<String> replies, Map<String, Object> data) {
			this.language = Objects.requireNonNull(language);
			this.text = Objects.requireNonNull(text);
			this.user = Objects.requireNonNull(user);
			this.source = Objects.requireNonNull(source);
			this.cmdSummary = cmdSummary;
			this.taggedText = taggedText;
			this.params = params;
			this.userLocation = userLocation;
			this.isMachineTranslated = isMachineTranslated;
			this.translatedFrom = translatedFrom;
			this.isPublic = isPublic;
			this.isLocal = isLocal;
			this.isExplicit = isExplicit;
			this.environment = environment;
			this.replies = replies;
			this.data = data;
			this.date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
		}

		@Override
		public String toString() {
			return text;
		}

	}

	//-----------------------------

	/**
	 * Add a "Sentence" (or multiple) to this command. 
	 */
	public void add(List<Sentence> sentences) {
		this.sentences.addAll(sentences);
	}

	/**
	 * Add a "Sentence" (or multiple) to this command. 
	 */
	public void setSentences(List<Sentence> sentences) {
		this.sentences = Objects.requireNonNull(sentences);
	}

	public List<Sentence> getSentences() {
		return sentences;
	}
	
	/**
	 * Return the command with all its different sentences as JSON string.
	 * @return String in JSON format
	 */
	public String toJsonString() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Fist gets the string via {@link #toJsonString()} and then parses the result to a JSONObject.<br>
	 * Note: this could/should be done in a more efficient way ...
	 */
	public JSONObject toJson() {
		return JSON.parseStringOrFail(toJsonString());
	}

}
