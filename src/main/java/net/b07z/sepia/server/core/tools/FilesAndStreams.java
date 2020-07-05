package net.b07z.sepia.server.core.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Handles file read/write/edit etc.
 * 
 * @author Florian Quirin
 *
 */
public class FilesAndStreams {
	
	private static Map<String, String> fileContentCache = new ConcurrentHashMap<>();
	
	//----helpers:
	
	/**
	 * Generic class to define line operations as lambda expressions. (Basically identical with Function<String, String>).
	 */
	public static interface LineOperation {
		/**
		 * Take line as input and return modified line.
		 */
		public String run(String lineInput);
	}
	
	/**
	 * Consume streams with custom consumer. Usage example with println:<br>
	 * <br>
	 * BufferedInputStreamConsumer streamGobbler = new StreamGobbler(myStream, System.out::println);<br>
	 * Executors.newSingleThreadExecutor().submit(streamGobbler);<br>
	 * <br>
	 * As seen on <a href="https://www.baeldung.com/run-shell-command-in-java">baeldung.com</a>
	 */
	public static class BufferedInputStreamConsumer implements Runnable {
	    private InputStream inputStream;
	    private Consumer<String> consumer;
	    private  Charset charset;
	 
	    public BufferedInputStreamConsumer(InputStream inputStream, Charset charset, Consumer<String> consumer) {
	        this.inputStream = inputStream;
	        this.consumer = consumer;
	        this.charset = charset;
	    }
	    @Override
	    public void run() {
	        new BufferedReader(new InputStreamReader(inputStream, charset)).lines()
	          .forEach(consumer);
	    }
	}

	/**
	 * Collect all data of an InputStream to a string via BufferedReader and InputStreamReader.<br>
	 * NOTE: Please check correct encoding of stream!
	 * @param stream - input stream
	 * @param charset - e.g.: StandardCharsets.UTF_8
	 * @param lineBreakChar - a character to use for line-breaks, e.g. "\n" or System.lineSeparator()
	 * @return
	 * @throws IOException
	 */
	public static String getStringFromStream(InputStream stream, Charset charset, String lineBreakChar) throws IOException{
		try (BufferedReader in = new BufferedReader(new InputStreamReader(stream, charset))) {
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine + lineBreakChar);
			}
			return response.toString();
		}catch (IOException e){
			throw e;
		}
	}
	/**
	 * Collect all data of an InputStream to a list, line-by-line via BufferedReader and InputStreamReader.<br>
	 * NOTE: Please check correct encoding of stream!
	 */
	public static List<String> getLinesFromStream(InputStream stream, Charset charset) throws IOException{
		try (BufferedReader in = new BufferedReader(new InputStreamReader(stream, charset))) {
			String inputLine;
			List<String> response = new ArrayList<>();
			while ((inputLine = in.readLine()) != null) {
				response.add(inputLine);
			}
			return response;
		}catch (IOException e){
			throw e;
		}
	}
	
	/**
	 * Get an List of "File"s from a directory path.
	 * @param directoryName - path to directory
	 * @param files - List of files to populate (or null -> creates ArrayList)
	 * @param doSubfolders - include sub-folders?
	 * @return list or null
	 */
	public static List<File> directoryToFileList(String directoryName, List<File> files, boolean doSubfolders) {
		File directory = new File(directoryName);
		if (files == null){
			files = new ArrayList<>();
		}
	
	    //get all the files from a directory
	    File[] fList = directory.listFiles();
	    if (fList == null){
	    	return null;
	    }
	    //System.out.println(directory.list().length);		//debug
	    for (File file : fList){
	        if (file.isFile()){
	            files.add(file);
	            //System.out.println(file.toString());		//debug
	        }else if (file.isDirectory() & doSubfolders){
	        	//listAllFiles(file.getAbsolutePath(), files, doSubfolders);
	        	directoryToFileList(file.getPath(), files, doSubfolders);
	        }
	    }
		return files;
	}
	/**
	 * Get an List of directories at path. Will skip files.
	 * @param path - path to search for directories
	 * @param directories - List of files to populate (or null -> creates ArrayList)
	 * @return list or null
	 */
	public static List<File> getDirectoriesAtPath(String path, List<File> directories) {
		File directory = new File(path);
		if (directories == null){
			directories = new ArrayList<>();
		}
	
	    //get everything at path
	    File[] fList = directory.listFiles();
	    if (fList == null){
	    	return null;
	    }
	    //System.out.println(directory.list().length);		//debug
	    for (File file : fList){
	        if (!file.isFile()){
	        	directories.add(file);
	            //System.out.println(file.toString());		//debug
	        }
	    }
		return directories;
	}
	
	/**
	 * Delete folder with all of it's content. This is necessary because Java's internal file.delete()
	 * cannot delete non-empty folders.
	 * @param folder - folder to delete
	 */
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	
	/**
	 * Read UTF-8 encoded file and return as list.<br>
	 * NOTE: You can use 'String.join("\n", lines)' to return a Sting of the file. 
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
	 * Write list to UTF-8 encoded file.
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
	 * Write an UTF-8 encoded content string to a file. Create all directories along the path. If the file exists overwrite it.
	 * @param path - path to where the file should be stored
	 * @param fileName - name of the file (with ending)
	 * @param utf8Content - content as UTF-8 encoded string
	 * @return
	 */
	public static boolean writeStringToFile(String path, String fileName, String utf8Content){
		try {
			Files.createDirectories(Paths.get(path));
			Files.write(Paths.get(path, fileName), utf8Content.getBytes(StandardCharsets.UTF_8));
			return true;
		
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Read UTF-8 encoded file line-by-line, do optional operation on each line and merge everything to one string.
	 * The result will be cached in a concurrent Map (optional) using the path as key and will be loaded from there on next call.<br>
	 * NOTE 1: If no lineOperation is given the lines will be merged via default UNIX end-of-line ('\n').<br>
	 * NOTE 2: Files can be large so think about your cache size when using this.<br>
	 * NOTE 3: The file will be loaded entirely and then iterated line-by-line ... not the best method for large files ...
	 * @param pathToFile - path to file, e.g. "Xtensions/myFolder/my.file"
	 * @param useCache - cache result and load from cache or don't 
	 * @param lineOperation - optional operation used to modify each line of the file (or null). NOTE: If you need an end-of-line character you need to add it in your operator.
	 * @return String representing the file after line-operations are applied and lines are merged
	 * @throws IOException
	 */
	public static String readFileModifyAndCache(String pathToFile, boolean useCache, LineOperation lineOperation) throws IOException {
		if (useCache && fileContentCache.containsKey(pathToFile)){
			return fileContentCache.get(pathToFile);
		}else{
			List<String> lines = readFileAsList(pathToFile);
			String mergedList;
			if (lineOperation != null){
				mergedList = "";
				for (String l : lines){
					l = lineOperation.run(l);
					mergedList += l;
				};
			}else{
				mergedList = String.join("\n", lines);
			}
			if (useCache){
				fileContentCache.put(pathToFile, mergedList);
			}
			return mergedList;
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
		try{
			Path path = Paths.get(pathWithName);
			List<String> fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
			boolean foundLine = false;
			for (int i=0; i<fileContent.size(); i++) {
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
		}catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Open a file, search line by regular expression then modify line by custom operation and store modifications.
	 * Stops after first match. If the line is not found append it ('oldLine' parameter will be empty string in this case).
	 * @param pathWithName - path to file including file-name
	 * @param lineMatchRegExp - regular expression to find line
	 * @param lineOperation - modify or create line with {@link LineOperation} (use lambda expression, e.g.: (oldLine) -> {	return newLine });
		})
	 * @return true (all good), false (error during read/write)
	 */
	public static boolean replaceLineOrAppend(String pathWithName, String lineMatchRegExp, LineOperation lineOperation){
		try{
			Path path = Paths.get(pathWithName);
			List<String> fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
			boolean foundLine = false;
			for (int i=0; i<fileContent.size(); i++) {
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
				String newLine = lineOperation.run("");
				fileContent.add(newLine);
				Files.write(path, fileContent, StandardCharsets.UTF_8);
				return true;
			}
		}catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Save settings to properties file (UTF-8).
	 * @param configFile - path and file
	 * @param config - Properties with settings to store
	 */
	public static void saveSettings(String configFile, Properties config) throws Exception{
		File f = new File(configFile);
		OutputStreamWriter out = new OutputStreamWriter(
				new FileOutputStream(f), StandardCharsets.UTF_8
		);
	    config.store(out, null);
	    out.flush();
	    out.close();
	}

	/**
	 * Load settings from properties file (UTF-8) and return Properties.
	 * @param configFile - path and file
	 */
	public static Properties loadSettings(String configFile) throws Exception{
		Properties config = new Properties();
		InputStreamReader stream = new InputStreamReader(
				new FileInputStream(configFile), StandardCharsets.UTF_8
		);
		config.load(stream);
		stream.close();
		return config;
	}
}
