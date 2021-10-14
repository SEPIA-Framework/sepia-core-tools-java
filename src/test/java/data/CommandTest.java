package data;

import net.b07z.sepia.server.core.data.Command;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class CommandTest {

	@Test
	public void testImportJSON() throws IOException {
		try (InputStream is = CommandTest.class.getResourceAsStream("/command.json")) {
			String exampleJson = IOUtils.toString(new InputStreamReader(is, StandardCharsets.UTF_8));
			Command cmd = Command.importAnswerJSON(exampleJson);
			// format as otherwise the string comparison will fail even for same JSONs:
			String jsonFromCommand = TestTools.asFormattedJson(cmd.toJsonString());
			String jsonFromExample = TestTools.asFormattedJson(exampleJson);
			assertEquals(jsonFromExample, jsonFromCommand);
		}
	}
	
}