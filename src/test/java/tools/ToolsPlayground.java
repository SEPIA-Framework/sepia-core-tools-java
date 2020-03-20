package tools;

import net.b07z.sepia.server.core.tools.Converters;
import net.b07z.sepia.server.core.tools.Security;

/**
 * Try some tools functions here to see if they do what they are supposed to ^^
 * @author Florian Quirin
 *
 */
public class ToolsPlayground {

	public static void main(String[] args) throws Exception {
		
		/* -- Runtime commands -- */
		/*
		System.out.println("Calling runtime: ");
		RuntimeResult rtr = RuntimeInterface.runCommand(new String[]{"chcp"}, 5000);
		System.out.println(rtr.toString()); if (rtr.getStatusCode() != 0) System.out.println(rtr.getException());
		rtr = RuntimeInterface.runCommand(new String[]{"echo", "Hello World!"}, 5000);
		System.out.println(rtr.toString()); if (rtr.getStatusCode() != 0) System.out.println(rtr.getException());
		*/
		/*
		rtr = RuntimeInterface.runCommand(new String[]{"ping", "sepia-framework.github.io"}, 5000);
		System.out.println(rtr.toString()); if (rtr.getStatusCode() != 0) System.out.println(rtr.getException());
		rtr = RuntimeInterface.runCommand(new String[]{"ping", "sepia-framework.github.io"}, 500);
		System.out.println(rtr.toString()); if (rtr.getStatusCode() != 0) System.out.println(rtr.getException());
		rtr = RuntimeInterface.runCommand(new String[]{"ping", "-c", "3", "sepia-framework.github.io"}, 5000);
		System.out.println(rtr.toString()); if (rtr.getStatusCode() != 0) System.out.println(rtr.getException());
		*/
		
		/* -- JSONWriter -- */
		/*
		System.out.println("\nJSONWriter test: ");
		JSONObject jo = JSON.make(
				"First", 10, 
				"Second", "20", 
				"Level2", JSON.make("Third", 300),
				"EmptyArray", new JSONArray(),
				"EmptyObject", new JSONObject()
		);
		System.out.println(JSONWriter.getPrettyString(jo));
		
		System.out.println(JSON.getObject(jo, "First".split("\\.")));
		System.out.println(JSON.getObject(jo, "Second".split("\\.")));
		System.out.println(JSON.getObject(jo, "EmptyArray".split("\\.")));
		System.out.println(JSON.getObject(jo, "EmptyObject".split("\\.")));
		System.out.println(JSON.getObject(jo, "Level2.Third".split("\\.")));
		
		JSONObject nodeResponseData = JSON.make(
				"hello", "Boss",
				"status", "success"
		);
		String answer = "Hello <result_hello> how are you?";
		String tag = "<result_hello>";
		String tagClean = tag.replaceFirst("<result_(.*?)>", "$1").trim();
		System.out.println(nodeResponseData.toJSONString());
		System.out.println(tagClean);
		String value = JSON.getObject(nodeResponseData, tagClean.split("\\.")).toString();
		answer = answer.replaceFirst("(<result_.*?>)", value);
		System.out.println(answer);
		
		String clusterKeyLight = "c";
		System.out.println((int) clusterKeyLight.charAt(clusterKeyLight.length()-1));
		*/
		
		/* -- Write test properties file -- */
		/*
		System.out.println("\nTesting properties store and load: ");
		Properties prop = new Properties();
		prop.setProperty("test", out);
		prop.setProperty("umlaute", "äöü");
		String path = System.getProperty("user.home") + "\\test.properties";
		try{
			FilesAndStreams.saveSettings(path, prop);
			System.out.println("Stored test-file at: " + path);
		}catch (Exception e){
			System.out.println("Failed to store test-file at: " + path);
			e.printStackTrace();
		}
		try{
			Properties prop2 = FilesAndStreams.loadSettings(path);
			System.out.println("Special Umlaute: " + prop2.getProperty("umlaute"));
			System.out.println("Test: " + prop2.getProperty("test"));
		}catch (Exception e){
			System.out.println("Failed to load test-file at: " + path);
			e.printStackTrace();
		}
		*/
		
		/* -- Password client hash -- */
		/*
		String hash = hashPassword("testpwd12345678!_");
		System.out.println("password client hash: " + hash);
		hash = hashPassword("TestPwd12345678!_");
		System.out.println("password client hash: " + hash);
		hash = hashPassword("TestPwd12345678!_%");
		System.out.println("password client hash: " + hash);
		hash = hashPassword("!§$%&/()=?`");
		System.out.println("password client hash: " + hash);
		hash = hashPassword("test12345!_");
		System.out.println("password client hash: " + hash);
		*/
		
		/* -- Converting numbers -- */
		
		System.out.println("Converting numbers:\n");
		System.out.println("2,000,000.5 --> " + Converters.stringToNumber("2,000,000.5").doubleValue());
		System.out.println("2000000.5 --> " + Converters.stringToNumber("2000000.5").doubleValue());
		System.out.println("2,5 --> " + Converters.stringToNumber("2,5").doubleValue());
		System.out.println("2.5 --> " + Converters.stringToNumber("2.5").doubleValue());
		
		System.out.println("21.57689 (#.##) --> " + Converters.numberToString(Converters.stringToNumber("21.57689"), "#.##"));
		System.out.println("21.57689 (#) --> " + Converters.numberToString(Converters.stringToNumber("21.57689"), "#"));
		System.out.println("1.57689 (##) --> " + Converters.numberToString(Converters.stringToNumber("1.57689"), "##"));
		System.out.println("300 (##.#) --> " + Converters.numberToString(Converters.stringToNumber("300"), "##.#"));
		System.out.println("30 (000.0#) --> " + Converters.numberToString(Converters.stringToNumber("30"), "000.0#"));
		System.out.println("30 (0.0) --> " + Converters.numberToString(Converters.stringToNumber("30"), "0.0"));
		System.out.println("1.57689 (0.0) --> " + Converters.numberToString(Converters.stringToNumber("1.57689"), "00.0"));
	}
	
	/* -- Password client hash -- */
	
	public static String hashPassword(String pwd){
		return Security.hashClientPassword(pwd);
	}
	
	

}
