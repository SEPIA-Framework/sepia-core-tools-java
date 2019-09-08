package net.b07z.sepia.server.core.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;
import java.util.Optional;

/**
 * Constants for languages for better type safety in Java (so that e.g. "en" isn't just
 * a String that gets easily confused with other strings).<br>
 * 
 * See: https://en.wikipedia.org/wiki/List_of_ISO_639-2_codes
 */
public enum Language {

	EN("English", "English", "eng"),
	DE("German", "Deutsch", "deu"),
	ES("Spanish", "español", "spa"),
	FR("French", "français", "fra"),
	IT("Italian", "italiano", "ita"),
	PT("Portuguese", "português", "por"),
	NL("Dutch", "Nederlands", "nld"),
	SV("Swedish", "svenska", "swe"),
	TR("Turkish", "Türkçe", "tur"),
	EL("Greek", "Νέα Ελληνικά", "ell"),
	PL("Polish", "Język polski", "pol"),
	HR("Croatian", "hrvatski", "hrv"),
	ZH("Chinese", "中文", "zho"),		//Mandarin: cmn ?
	JA("Japanese", "日本語", "jpn"),
	KO("Korean", "한국어", "kor"),
	RU("Russian", "русский язык", "rus"),
	AR("Arabic", "العَرَبِيَّة", "ara");
	
	private final String englishFullName;
	private final String translatedFullName;
	private final String iso639_3;

	/**
	 * Build language object.
	 * @param englishFullName - name in English
	 * @param translatedFullName - name in native language
	 * @param iso639_3 - 3 letters ISO 639-3 code
	 */
	Language(String englishFullName, String translatedFullName, String iso639_3) {
		this.englishFullName = Objects.requireNonNull(englishFullName);
		this.translatedFullName = translatedFullName;
		this.iso639_3 = Objects.requireNonNull(iso639_3);
	}
	
	/**
	 * Get enum value from language code string (or throw error).
	 * @param languageCode - e.g. "en" or "DE" (case independent)
	 */
	public static Language from(String languageCode) {
		return Language.valueOf(languageCode.toUpperCase());
	}

	// used so "en" can be in the JSON data but the enum can be called "EN":
	@JsonCreator
	public static Language forValue(String value) {
		return Language.valueOf(value.toUpperCase());
	}

	/**
	 * Use this to get e.g. "de" for German, "en" for English.
	 */
	@JsonValue
	public String toValue() {
		return name().toLowerCase();
	}

	/**
	 * Language in native spelling. Kann include special characters, e.g.: 日本語 (Japanese).
	 */
	public String getTranslatedFullName() {
		return translatedFullName;
	}

	/**
	 * Full English name of the language.
	 */
	public Optional<String> getEnglishFullName() {
		return Optional.ofNullable(englishFullName);
	}

	/**
	 * A three-character code as defined by ISO 639-3.<br>
	 * See: https://en.wikipedia.org/wiki/List_of_ISO_639-2_codes 
	 */
	public String getIso639Code() {
		return iso639_3;
	}
	
}
