package net.b07z.sepia.server.core.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Simple but solid CSV reader.<br>
 * <br>
 * Found at: https://mkyong.com/java/how-to-read-and-parse-csv-file-in-java/<br> 
 * Inspired by: https://agiletribe.wordpress.com/2012/11/23/the-only-class-you-need-for-csv-files/
 * Modified by: Florian Quirin 
 * 
 */
public class CsvUtils {

    public static final char DEFAULT_SEPARATOR = ',';
    public static final char DEFAULT_QUOTE = '"';

    /**
     * Read a file line-by-line and return result as rows.
     * @param path - Path of file including file name
     * @param separator - CSV column separator, e.g. ',' (DEFAULT_SEPARATOR)
     * @param customQuote - CSV quote char, e.g. '"' (DEFAULT_QUOTE)
     * @param filter - optional line filter function (or null). Input is row as List. Return true to keep line, false to skip.
     * @return
     * @throws RuntimeException
     * @throws IOException
     */
    public static List<List<String>> readFileAsRows(String path, char separator, char customQuote, Predicate<List<String>> filter) 
    		throws RuntimeException, IOException {
    	List<List<String>> data = new ArrayList<>();
    	try (Stream<String> stream = Files.lines(Paths.get(path))){
			data = readStreamAsRows(stream, separator, customQuote, filter);
		}
    	return data;
    }
    /**
     * Read a stream line-by-line and return result as rows.
     * @param stream - Steam created from a file or any source. Elements represent lines of data.
     * @param separator - CSV column separator, e.g. ',' (DEFAULT_SEPARATOR)
     * @param customQuote - CSV quote char, e.g. '"' (DEFAULT_QUOTE)
     * @param filter - optional line filter function (or null). Input is row as List. Return true to keep line, false to skip.
     * @return
     * @throws RuntimeException
     * @throws IOException
     */
    public static List<List<String>> readStreamAsRows(Stream<String> stream, char separator, char customQuote, Predicate<List<String>> filter) 
    		throws RuntimeException {
    	List<List<String>> data = new ArrayList<>();
		stream.forEachOrdered((line) -> {
			try{
				List<String> row = parseLine(line, separator, customQuote);
				if (filter != null){
					if (filter.test(row)){
						data.add(row);
					}
				}else{
					data.add(row);
				}
			}catch (Exception e){ 
				throw new RuntimeException("Error in Line " + (data.size() + 1) + ": " + e.getMessage());
			}
		});
    	return data;
    }

    /**
     * Parse a line using the DEFAULT_SEPARATOR ',' and DEFAULT_QUOTE char '"'.
     * @param cvsLine - String representing the line of the CSV file
     * @return List of strings representing each column of a row 
     */
    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    /**
     * Parse a line using a custom separator and DEFAULT_QUOTE char '"'.
     * @param cvsLine - String representing the line of the CSV file
     * @param separator - custom separator, e.g. ';' or '|'
     * @return List of strings representing each column of a row
     */
    public static List<String> parseLine(String cvsLine, char separator) {
        return parseLine(cvsLine, separator, DEFAULT_QUOTE);
    }

    /**
     * Parse a line using a custom separator and custom quote char.
     * @param cvsLine - String representing the line of the CSV file
     * @param separator - custom separator, e.g. ';' or '|'
     * @param customQuote - custom quote character, e.g. '\''
     * @return
     */
    public static List<String> parseLine(String cvsLine, char separator, char customQuote) {
        List<String> result = new ArrayList<>();

        //if empty return null to avoid array length issues
        if (cvsLine == null || cvsLine.isEmpty()){
            return null;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars){
            if (inQuotes){
                startCollectChar = true;
                if (ch == customQuote){
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                }else{
                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"'){
                        if (!doubleQuotesInColumn){
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    }else{
                        curVal.append(ch);
                    }
                }
            }else{
                if (ch == customQuote){
                    inQuotes = true;
                    
                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"'){
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar){
                        curVal.append('"');
                    }
                }else if (ch == separator){
                    result.add(curVal.toString());
                    curVal = new StringBuffer();
                    startCollectChar = false;
                }else if (ch == '\r'){
                    //ignore LF characters
                    continue;
                }else if (ch == '\n'){
                    //the end, break!
                    break;
                }else{
                    curVal.append(ch);
                }
            }
        }
        result.add(curVal.toString());
        return result;
    }
}
