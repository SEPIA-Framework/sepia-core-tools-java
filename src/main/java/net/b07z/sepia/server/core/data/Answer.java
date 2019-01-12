package net.b07z.sepia.server.core.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import net.b07z.sepia.server.core.server.ConfigDefaults;
import net.b07z.sepia.server.core.tools.JSON;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

/**
 * This class contains all required info for answers given by the assistant.
 * Answers can have a number of parameters like character (neutral, cool, ...), when to trigger (first time, first repetition, etc.)
 * and many more. If you create a custom answer pool (e.g. inside a service) use this constructor to get a good set of parameters:<br>
 * Answer(Language language, String type, String text, Character character, int repetition, int mood)
 * 
 * @author Florian Quirin
 *
 */
public class Answer {

	//database types to organize commands
	public static final String ANSWERS_INDEX = "answers";
	public static final String ANSWERS_TYPE = "all";
	
	// NOTE: keep in sync with answers-mapping.json! Only this way we can use Jackson to automatically 
	// serialize and deserialize from/to JSON.
	private Language language;
	private String type;
	private String text;
	private int repetition;					//default: 0
	private int mood;								//default: 5
	private List<Character> characters;
	
	private String user;
	private String source;
	private boolean publicAnswer;
	private boolean localAnswer;
	private boolean explicitAnswer;
	private String userLocation;
	private boolean machineTranslated;
	private String translatedFrom;
	private String environment;
	private List<String> tags;
	private Map<String, Object> data;		//any additional data we did not think of right now
	private String date;

	/**
	 * Every answer can be assigned to a certain assistant 'character' like 'cool' or 'rude' (or 'neutral').
	 */
	public enum Character {
		neutral, rude, cool, polite
	}
	
	/**
	 * Usually you want to use another constructor: {@link Answer}(Language language, String type, String text, Character character, int repetition, int mood)
	 */
	public Answer() {
		// needed for Jackson
	}
	
	/**
	 * Create an answer with default settings.
	 * @param language - language code for this answer
	 * @param type - answer key, basically an ID to find an answer in the pool
	 * @param text - actual answer (variables like &lt1&gt, &lt2&gt ... are supported)
	 */
	public Answer(Language language, String type, String text) {
		this(language, type, text, Character.neutral);
	}
	/**
	 * Create an answer with default settings and custom character.
	 * @param language - language code for this answer
	 * @param type - answer key, basically an ID to find an answer in the pool
	 * @param text - actual answer (variables like &lt1&gt, &lt2&gt ... are supported)
	 * @param character - answer {@link Character} like "neutral", "rude", "cool"
	 */
	public Answer(Language language, String type, String text, Character character) {
		this(language, type, text, character, 0, 5);
	}
	
	/**
	 * Build a new answer with all relevant information.
	 * @param language - language code for this answer
	 * @param type - answer key, basically an ID to find an answer in the pool
	 * @param text - actual answer (variables like &lt1&gt, &lt2&gt ... are supported)
	 * @param character - answer {@link Character} like "neutral", "rude", "cool"
	 * @param repetition - when will this answer be triggered? First try (0), first repeat (1), following repeats (2)
	 * @param mood - mood level that this answer will be used from 0-10. Sad (0), neutral (5), happy (10)
	 */
	public Answer(Language language, String type, String text, Character character, int repetition, int mood) {
		this(language, type, text, Collections.singletonList(character), repetition, mood);
	}
	/**
	 * Build a new answer with all relevant information.
	 * @param language - language code for this answer
	 * @param type - answer key, basically an ID to find an answer in the pool
	 * @param text - actual answer (variables like &lt1&gt, &lt2&gt ... are supported)
	 * @param characters - multiple answer {@link Character} like "neutral", "rude", "cool"
	 * @param repetition - when will this answer be triggered? First try (0), first repeat (1), following repeats (2)
	 * @param mood - mood level that this answer will be used from 0-10. Sad (0), neutral (5), happy (10)
	 */
	public Answer(Language language, String type, String text, List<Character> characters, int repetition, int mood) {
		this(language, type, text, characters, repetition, mood,
				ConfigDefaults.defaultAssistantUserId, Defaults.IMPORT_SOURCE, true, false, false, null,
				false, null, "all", null, null
			);
	}

	/**
	 * Usually you want to use another constructor: {@link Answer}(Language language, String type, String text, Character character, int repetition, int mood)
	 * @param isPublic is this visible for everybody, if not, it's a private answer
	 * @param isLocal is this run on the local home server?
	 */
	public Answer(Language language, String type, String text, List<Character> characters, int repetition, int mood,
					String user, String source, boolean isPublic, boolean isLocal, boolean isExplicit, String userLocation, 
					boolean isMachineTranslated, String translatedFrom,	String environment, List<String> tags, Map<String, Object> data) 
	{
		this.language = Objects.requireNonNull(language, "Language was null for text: " + text);
		this.type = type;
		this.text = Objects.requireNonNull(text, "Text was null for type: " + type + ", language: " + language);
		if (text.trim().isEmpty()) {
			throw new IllegalArgumentException("Text was empty or whitespace only for type: " + type + ", language: " + language);
		}
		this.characters = characters;
		this.repetition = repetition;
		this.mood = mood;
		
		this.user = user;
		this.source = source;
		this.publicAnswer = isPublic;
		this.localAnswer = isLocal;
		this.explicitAnswer = isExplicit;
		this.userLocation = userLocation;
		this.machineTranslated = isMachineTranslated;
		this.translatedFrom = translatedFrom;
		this.environment = environment;
		this.tags = tags;
		this.data = data;
		this.date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
	}

	/**
	 * Language in use for this answer.
	 */
	public Language getLanguage() {
		return language;
	}

	/**
	 * This is the answer key, basically an ID to find an answer or list of answers in the pool (DB, file or memory).
	 */
	public String getType() {
		return type;
	}

	/**
	 * The actual answer in the given language.
	 */
	public String getText() {
		return text;
	}

	public int getRepetition() {
		return repetition;
	}

	public int getMood() {
		return mood;
	}
	
	public List<Character> getCharacters() {
		return Collections.unmodifiableList(characters);
	}
	
	public String getUser() {
		return user;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getEnvironment() {
		return environment;
	}
	
	public boolean isPublicAnswer() {
		return publicAnswer;
	}
	
	public boolean isLocalAnswer() {
		return localAnswer;
	}
	
	public boolean isExplicitAnswer() {
		return explicitAnswer;
	}
	
	public String getUserLocation() {
		return userLocation;
	}
	
	public boolean isMachineTranslated() {
		return machineTranslated;
	}
	
	public String getTranslatedFrom() {
		return translatedFrom;
	}
	
	public String getDate(){
		return date;
	}
	
	public List<String> getTags(){
		return tags;
	}
	
	public Map<String, Object> getData(){
		return data;
	}
	
	/*
	public HashMap<String, Object> getDetails(){
		HashMap<String, Object> details = new HashMap<>();
		details.put("public", isPublic);
		details.put("local", isLocal);
		details.put("explicit", isExplicit);
		details.put("user_location", userLocation);
		details.put("machine_translated", isMachineTranslated);
		details.put("translated_from", translatedFrom);
		details.put("environment", environment);
		details.put("date", date);
		details.put("data", data);
		return details;
	}
	*/
	
	//-----
	
	/**
	 * Import an Answer that comes in the (old) string format as used in text files.  
	 * @param line - line of text file (type;; rep=0|mood=5;; the answer;; char=cool,neutral)
	 * @param language - language code to use on creation of Answer
	 */
	public static Answer importAnswerString(String line, Language language, boolean isMachineTranslated){
		String[] parts = line.replaceAll("(char=.*?)#.*", "$1").trim().split(";;\t*");
		String type = parts[0].trim();
		String rep = parts[1].split("\\|")[0].replace("rep=", "").trim();
		String mood = parts[1].split("\\|")[1].replace("mood=", "").trim();
		String text = parts[2].trim();
		List<String> characterStr;
		List<Answer.Character> characters;
		if (parts.length >= 4) {
			characterStr = Arrays.asList(parts[3].replace("char=", "").split(","));
			characters = characterStr.stream()
					.filter(f -> !f.isEmpty())
					.map(f -> Answer.Character.valueOf(f.trim()))
					.collect(Collectors.toList());
		} else {
			characters = new ArrayList<>();
		}
		String translatedFrom = null;
		if (line.contains("#source:")) {
			translatedFrom = line.replaceAll(".*#source:(.*?)#?", "$1").trim();
		}
		Answer answer = new Answer(language, type, text, characters, Integer.parseInt(rep), Integer.parseInt(mood),
				ConfigDefaults.defaultAssistantUserId, Defaults.IMPORT_SOURCE, true, false, false, null, 
				isMachineTranslated, translatedFrom, "all", null, null);
		
		return answer;
	}
	
	/**
	 * Load Answer from JSON string.
	 * @param json - string with Answer in JSON format (as exported by toJsonString()).
	 */
	public static Answer importAnswerJSON(String json) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		Answer answer = mapper.readValue(json, Answer.class);
		return answer;
	}
	/**
	 * Load Answer from JSON.
	 * @param json - string with Answer in JSON format (as exported by toJsonString()).
	 */
	public static Answer importAnswerJSON(JSONObject json) throws IOException{
		//Unfortunately this method has to convert back to string first :-(
		return importAnswerJSON(json.toJSONString());
	}

	/**
	 * Return object as JSON formatted string (or throw error).
	 */
	public String toJsonString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializing " + this + " to JSON", e);
		}
	}
	/**
	 * Convert object first to JSON string and then to JSONObject. Kind of stupid I know, but ...<br>
	 * Throws error on parsing exception.
	 */
	public JSONObject toJson(){
		return JSON.parseStringOrFail(toJsonString());
	}

	@Override
	public String toString() {
		return language + ":" + type + ":" + text;
	}
}
