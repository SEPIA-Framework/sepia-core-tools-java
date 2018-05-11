package net.b07z.sepia.server.core.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import net.b07z.sepia.server.core.data.Answer;
import net.b07z.sepia.server.core.data.Language;
import net.b07z.sepia.server.core.database.DatabaseInterface;
import net.b07z.sepia.server.core.tools.Debugger;
import net.b07z.sepia.server.core.tools.FilesAndStreams;

/**
 * Import answers stored in a file to database (usually Elasticsearch).
 */
public class AnswerImporter {
	
	//Database
	DatabaseInterface database;
	
	/**
	 * Construct importer by linking database implementation.
	 * @param database - {@link DatabaseInterface}
	 */
	public AnswerImporter(DatabaseInterface database){
		this.database = database;
	}

	/**
	 * Run importer for one file. Imports answer for the default system user (usually assistant userId). 
	 * @param file - file containing answers for one language
	 * @param language - language of the file
	 * @param isMachineTranslated - when you are using files that have been translated by a machine
	 * @throws IOException
	 */
	public void run(File file, Language language, boolean isMachineTranslated) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		int n = 0;
		for (String line : lines) {
			Answer answer = Answer.importAnswerString(line, language, isMachineTranslated);
			//System.out.println(answer);
			database.setAnyItemData(Answer.ANSWERS_INDEX, Answer.ANSWERS_TYPE, answer.toJson());		
			//System.out.println(answer.toJsonString());
			n++;
		}
		Debugger.println("Imported answers: " + n, 3);
	}
	
	/**
	 * Import all files in a folder that follow the format "answers_xy" for the default user (usually assistant userId). 
	 * where "xy" is a valid language code (e.g. "en" or "de").
	 * @param pathToFolder - folder with answers
	 * @param isMachineTranslated - are these answers translated by a machine?
	 * @throws IOException 
	 */
	public void loadFolder(String pathToFolder, boolean isMachineTranslated) throws IOException{
		List<File> files = FilesAndStreams.directoryToFileList(pathToFolder, null, false);
		for (File f : files){
			String fileName = f.getName();
			if (fileName.matches("answers_\\w\\w(\\..*|$)")){
				String languageCode = fileName.replaceFirst("answers_(\\w\\w).*", "$1");
				Language lang = Language.from(languageCode); 	//makes sure that the value is valid
				Debugger.println("Importing answers from file '" + fileName + "' for language: " + lang.toValue(), 3);
				run(f, lang, isMachineTranslated);
			}
		}
	}
	
}
