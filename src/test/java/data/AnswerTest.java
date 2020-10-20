package data;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import net.b07z.sepia.server.core.data.Answer;
import net.b07z.sepia.server.core.data.Language;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;

public class AnswerTest {

	@Test
	public void testImportJSON() throws IOException {
		try (InputStream is = AnswerTest.class.getResourceAsStream("/answer.json")) {
			String exampleJson = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
			
			Answer answer = Answer.importAnswerJSON(exampleJson);
			//System.out.println("testImportJSON: " + answer.toJsonString()); 			//debug
			
			assertThat(answer.getType(), is("good_answers"));
			assertThat(answer.getText(), is("this is the answer man"));
			assertThat(answer.getRepetition(), is(0));
			assertThat(answer.getMood(), is(5));
			assertThat(answer.getCharacters().toString(), is("[rude, cool]"));
			assertTrue(answer.isExplicitAnswer());
			assertFalse(answer.isPublicAnswer());
			assertTrue(answer.toJsonString().contains("\"type\":\"good_answers\""));
			assertTrue(answer.toJsonString().contains("this is the answer"));
			assertTrue(answer.toJsonString().contains("fakeuser@example.com"));
		}
	}

	@Test
	public void testToJson() throws IOException {
		Answer answer = new Answer(Language.EN, "myType", "myText", Answer.Character.cool);
		String json = answer.toJsonString();
		//System.out.println("testToJSON: " + json); 			//debug
		assertTrue(json.contains("\"language\":\"en\""));
		assertTrue(json.contains("\"public_answer\":true"));
	}

}
