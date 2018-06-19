package net.b07z.sepia.server.core.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Handles file read/write/edit etc.
 * 
 * @author Florian Quirin
 *
 */
public class FilesAndStreams {
	
	//----helpers:
	
	/**
	 * Generic class to give line operations as lambda expressions.
	 */
	public static interface LineOperation {
		/**
		 * Take line as input and return modified line.
		 */
		public String run(String lineInput);
	}

	/**
	 * Collect all data of an InputStream to a string. 
	 */
	public static String getStringFromStream(InputStream stream) {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			return response.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Get an ArrayList of "File"s from a directory path.
	 * @param directoryName - path to directory
	 * @param files - ArrayList of files to populate (or null -> creates ArrayList)
	 * @param doSubfolders - include sub-folders?
	 * @return list or null
	 */
	public static List<File> directoryToFileList(String directoryName, List<File> files, boolean doSubfolders) {
		File directory = new File(directoryName);
		if (files == null){
			files = new ArrayList<>();
		}
	
	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    if (fList == null){
	    	return null;
	    }
	    //System.out.println(directory.list().length);		//debug
	    for (File file : fList) {
	        if (file.isFile()) {
	            files.add(file);
	            //System.out.println(file.toString());		//debug
	        } else if (file.isDirectory() & doSubfolders) {
	        	//listAllFiles(file.getAbsolutePath(), files, doSubfolders);
	        	directoryToFileList(file.getPath(), files, doSubfolders);
	        }
	    }
		return files;
	}
	
	/**
	 * Read file and return as list.
	 * @param pathWithName - path to file including file-name
	 * @return list with file content line-by-line
	 * @throws IOException
	 */
	public static List<String> readFileAsList(String pathWithName) throws IOException{
		Path path = Paths.get(pathWithName);
		List<String> fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
		return fileContent;
	}
	
	/**
	 * Write list to file.
	 * @param pathWithName - path to file including file-name
	 * @param fileContent - list of strings to be stored line-by-line
	 * @return true (all good), false (error during write)
	 */
	public static boolean writeFileFromList(String pathWithName, List<String> fileContent){
		try {
			Path path = Paths.get(pathWithName);
			Files.write(path, fileContent, StandardCharsets.UTF_8);
			return true;
		
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Open a file, search line by regular expression then replace with new line.
	 * Stops after first match.
	 * @param pathWithName - path to file including file-name
	 * @param lineMatchRegExp - regular expression to find line
	 * @param replacement - complete line is replaced by this
	 * @return true (all good), false (error during read/write or line not found)
	 */
	public static boolean replaceLineInFile(String pathWithName, String lineMatchRegExp, String replacement){
		return replaceLineInFile(pathWithName, lineMatchRegExp, (oldLine) -> {
			return replacement;
		});
	}
	/**
	 * Open a file, search line by regular expression then modify line by custom operation and store modifications.
	 * Stops after first match.
	 * @param pathWithName - path to file including file-name
	 * @param lineMatchRegExp - regular expression to find line
	 * @param lineOperation - modify line by using this with {@link LineOperation} (use lambda expression)
	 * @return true (all good), false (error during read/write or line not found)
	 */
	public static boolean replaceLineInFile(String pathWithName, String lineMatchRegExp, LineOperation lineOperation){
		try {
			Path path = Paths.get(pathWithName);
			List<String> fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
			boolean foundLine = false;
			for (int i = 0; i < fileContent.size(); i++) {
				String line = fileContent.get(i);
			    if (line.matches(lineMatchRegExp)) {
			    	foundLine = true;
			    	String newLine = lineOperation.run(line);
			        fileContent.set(i, newLine);
			        break;
			    }
			}
			if (foundLine){
				Files.write(path, fileContent, StandardCharsets.UTF_8);
				return true;
			}else{
				throw new RuntimeException("Line matching regular expression NOT found in: " + pathWithName);
			}
		
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Save settings to properties file.
	 * @param config_file - path and file
	 * @param config - Properties with settings to store
	 */
	public static void saveSettings(String config_file, Properties config) throws Exception{
		OutputStream out =null;
		File f = new File(config_file);
	    out = new FileOutputStream( f );
	    config.store(out, null);
	    out.flush();
	    out.close();
	}

	/**
	 * Load settings from properties file and return Properties.
	 * @param config_file - path and file
	 */
	public static Properties loadSettings(String config_file) throws Exception{
		BufferedInputStream stream=null;
		Properties config = new Properties();
		stream = new BufferedInputStream(new FileInputStream(config_file));
		config.load(stream);
		stream.close();
		return config;
	}
}
