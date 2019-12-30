package net.b07z.sepia.server.core.data;

import java.util.*;

import org.json.simple.JSONObject;

/**
 * Build a sentence for a {@link Command}.
 */
public class SentenceBuilder {

	private final String text;
	private final String user;
	private final String source;

	private Language language;
	private String environment;
	private String deviceId;
	private String taggedText;
	private String cmdSummary;
	private JSONObject params = new JSONObject();
	private boolean isMachineTranslated;
	private List<String> replies = new ArrayList<>();
	private boolean isPublic;
	private boolean isLocal;
	private boolean isExplicit;
	private String userLocation;
	private String translatedFrom;
	private JSONObject data = new JSONObject();

	public SentenceBuilder(String text, String user, String source) {
		this.text = Objects.requireNonNull(text);
		this.user = Objects.requireNonNull(user);
		this.source = Objects.requireNonNull(source);
	}

	public SentenceBuilder setEnvironment(String env) {
		this.environment = env;
		return this;
	}
	
	public SentenceBuilder setDeviceId(String devId) {
		this.deviceId = devId;
		return this;
	}
	
	public SentenceBuilder setCmdSummary(String cmd_summary) {
		this.cmdSummary = cmd_summary;
		return this;
	}

	public SentenceBuilder setTaggedText(String taggedText) {
		this.taggedText = taggedText;
		return this;
	}

	public SentenceBuilder setParams(JSONObject params) {
		this.params = params;
		return this;
	}
	
	public SentenceBuilder setMachineTranslated(boolean machineTranslated) {
		this.isMachineTranslated = machineTranslated;
		return this;
	}
	
	public SentenceBuilder setData(JSONObject data) {
		this.data = data;
		return this;
	}

	public SentenceBuilder setReplies(List<String> replies) {
		this.replies = replies;
		return this;
	}
	
	public SentenceBuilder setPublic(boolean isPublic) {
		this.isPublic = isPublic;
		return this;
	}
	
	public SentenceBuilder setLocal(boolean isLocal) {
		this.isLocal = isLocal;
		return this;
	}
	
	public SentenceBuilder setExplicit(boolean isExplicit) {
		this.isExplicit = isExplicit;
		return this;
	}

	public SentenceBuilder setUserLocation(String location) {
		this.userLocation = location;
		return this;
	}

	public SentenceBuilder setTranslatedFrom(String translatedFrom) {
		this.translatedFrom = translatedFrom;
		return this;
	}

	public SentenceBuilder setLanguage(Language language) {
		this.language = language;
		return this;
	}

	public Command.Sentence build() {
		return new Command.Sentence(language, text, user, source, taggedText, params, cmdSummary,
				userLocation, isMachineTranslated, translatedFrom, isPublic, isLocal, isExplicit, environment, deviceId, replies, data);
	}
}
