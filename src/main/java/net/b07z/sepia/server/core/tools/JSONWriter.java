package net.b07z.sepia.server.core.tools;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Creates pretty strings from JSON objects.
 * Sample usage:
 * <pre>
 * Writer writer = new JSONWriter();
 * jsonobject.writeJSONString(writer);
 * System.out.println(writer.toString());
 * OR:
 * String s = JSONWriter.getPrettyString(jsonObject);
 * </pre>
 * 
 * @author Elad Tabak
 * @author Maciej Komosinski, minor improvements, 2015
 * @author Florian Quirin, minor improvements, 2018
 * @since 28-Nov-2011
 * @version 0.3
 */
public class JSONWriter extends StringWriter
{
	final String indentstring;
	final String spaceaftercolon;

	private int indentlevel = 0;
	
	/**
	 * Default constructor with 4 space indentation and 1 space after colons.
	 */
	public JSONWriter(){
		this.indentstring = "    "; 		//default indentation
		this.spaceaftercolon = " "; 	//use "" if you don't want space after colon
	}
	/**
	 * Constructor with custom indentation and space-after-colon string.
	 * @param indentation - default: "    " (4 spaces for next indentation level)
	 * @param colonSpace - default: " " (1 space after each colon)
	 */
	public JSONWriter(String indentation, String colonSpace){
		this.indentstring = indentation;
		this.spaceaftercolon = colonSpace;
	}
	
	/**
	 * Get default, pretty JSON string. Returns null on error!
	 * @param jo - JSONObject to print pretty
	 * @return String or null
	 */
	public static String getPrettyString(JSONObject jo){
		Writer sw = new JSONWriter();
		try {
			jo.writeJSONString(sw);
			return sw.toString()
					.replaceAll("\\[(\\n|\\s)+]", "[]")
					.replaceAll("\\{(\\n|\\s)+}", "{}");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Get default, pretty JSON string. Returns null on error!
	 * @param ja - JSONArray to print pretty
	 * @return String or null
	 */
	public static String getPrettyString(JSONArray ja){
		Writer sw = new JSONWriter();
		try {
			ja.writeJSONString(sw);
			return sw.toString()
					.replaceAll("\\[(\\n|\\s)+]", "[]")
					.replaceAll("\\{(\\n|\\s)+}", "{}");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void write(int c)
	{
		char ch = (char) c;
		if (ch == '[' || ch == '{')	{
			super.write(c);
			super.write('\n');
			indentlevel++;
			writeIndentation();
		} else if (ch == ',') {
			super.write(c);
			super.write('\n');
			writeIndentation();
		} else if (ch == ']' || ch == '}') {
			super.write('\n');
			indentlevel--;
			writeIndentation();
			super.write(c);
		} else if (ch == ':') {
			super.write(c);
			super.write(spaceaftercolon);
		} else {
			super.write(c);
		}

	}
	private void writeIndentation() {
		for (int i = 0; i < indentlevel; i++) {
			super.write(indentstring);
		}
	}
}
