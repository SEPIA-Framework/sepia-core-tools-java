package data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public final class TestTools {

	private TestTools() {
	}

	public static String asFormattedJson(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
		Object jsonNode = mapper.readValue(json, Object.class);
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
	}

}
