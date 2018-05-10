package net.b07z.sepia.server.core.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import net.b07z.sepia.server.core.data.Answer;
import net.b07z.sepia.server.core.data.Language;
import net.b07z.sepia.server.core.database.DatabaseInterface;

/**
 * Import answers stored in a file to Elasticsearch.
 */
public class AnswerImporter {

	public static void run(File file, Language language, DatabaseInterface es, boolean isMachineTranslated) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		for (String line : lines) {
			Answer answer = Answer.importAnswerString(line, language, isMachineTranslated);
			System.out.println(answer);
			es.setAnyItemData(Answer.ANSWERS_INDEX, Answer.ANSWERS_TYPE, answer.toJson());		
			//System.out.println(answer.toJsonString());
		}
	}
	
}
