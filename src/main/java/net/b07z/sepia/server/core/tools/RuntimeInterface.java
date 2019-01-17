package net.b07z.sepia.server.core.tools;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Class to help with runtime executions.
 * 
 * @author Florian Quirin
 *
 */
public class RuntimeInterface {
	
	public static Boolean isWindows = null;
	public static Charset windowsShellCodepage = null; 
	
	/**
	 * Hold result of a runtime command.
	 */
	public static class RuntimeResult {
		private int statusCode;
		private List<String> output;
		private Exception exception;
		
		/**
		 * Create new result with statusCode (kind of exit code) and output string.
		 * @param statusCode - 0: all good, 1: general error, 2: tbd, 3: timeout
		 * @param output - list of lines returned by execution (in correct encoding hopefully) or null
		 * @param exception - exception catched if any (or null)
		 */
		public RuntimeResult(int statusCode, List<String> output, Exception exception){
			this.statusCode = statusCode;
			this.output = output;
			this.exception = exception;
		}
		/**
		 * Return list of lines.
		 */
		public List<String> getOutput(){
			return output;
		}
		/**
		 * Return status code - 0: all good, 1: general error, 2: tbd, 3: timeout
		 */
		public int getStatusCode(){
			return statusCode;
		}
		public Exception getException(){
			return exception;
		}
		@Override
		public String toString(){
			return toString(System.lineSeparator());
		}
		/**
		 * Return output as string. If possible (not null or empty) join lines separated by 'delimiter'.
		 * @param delimiter
		 * @return
		 */
		public String toString(CharSequence delimiter){
			if (output == null){
				return null;
			}else if (output.isEmpty()){
				return "";
			}else{
				return String.join(delimiter, output);
			}
		}
	}
	
	/**
	 * Is the runtime a Windows OS?
	 * @return
	 */
	public static boolean isWindows(){
		if (isWindows == null){
			isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		}
		return isWindows;
	}
	
	/**
	 * We need to run the shell codepage command to get proper windows encoding (is there REALLY no BETTER WAY??).
	 * @return
	 */
	public static RuntimeResult getWindowsShellCodepage(){
		RuntimeResult rtr = runCommand(new String[]{"chcp"}, 3000);
		List<String> out = rtr.getOutput();
		if (rtr.getStatusCode() == 0 && Is.notNullOrEmpty(out)){
			Debugger.println("RuntimeInterface - Trying to get Windows shell encoding...", 3);
			String joinedOut = rtr.toString().replaceAll("\\n|\\r", " ");
			if (joinedOut.matches(".*\\d+.*")){
				String encoding = joinedOut.replaceAll(".*?([0-9]+).*", "$1");
				windowsShellCodepage = Charset.forName("cp" + encoding);
				Debugger.println("RuntimeInterface - Windows shell encoding: " + windowsShellCodepage.toString(), 3);
			}else{
				return new RuntimeResult(1, null, new RuntimeException("Could not get Windows shell encoding (codepage)!"));
			}
		}else{
			return new RuntimeResult(1, null, new RuntimeException("Could not get Windows shell encoding (codepage)!"));
		}
		return rtr;
	}
	
	/**
	 * Execute runtime command. Chooses between "cmd.exe" and "sh" shell by OS.
	 * @param command - e.g.: 'new String[]{"ping", "-c", "3", "sepia-framework.github.io"}'
	 * @return
	 */
	public static RuntimeResult runCommand(String[] command){
		return runCommand(command, 5000);
	}
	/**
	 * Execute runtime command. Chooses between "cmd.exe" and "sh" shell by OS.
	 * @param command - e.g.: 'Arrays.asList("ping", "-c", "3", "sepia-framework.github.io")'
	 * @param customTimeout - custom value between 0 and 15000 ms
	 * @return
	 */
	public static RuntimeResult runCommand(Collection<String> command, long customTimeout){
		return runCommand(command.toArray(new String[command.size()]), 5000);
	}
	/**
	 * Execute runtime command. Chooses between "cmd.exe" and "sh" shell by OS.
	 * @param command - e.g.: 'new String[]{"ping", "-c", "3", "sepia-framework.github.io"}'
	 * @param customTimeout - custom value between 0 and 15000 ms
	 * @return
	 */
	public static RuntimeResult runCommand(String[] command, long customTimeout){
		Charset encoding = StandardCharsets.UTF_8;
		if (isWindows() && windowsShellCodepage == null && !command[0].equals("chcp")){
			RuntimeResult rtr = getWindowsShellCodepage(); 
			if (rtr.getStatusCode() != 0){
				return new RuntimeResult(1, null, rtr.getException());
			}else{
				encoding = windowsShellCodepage;
			}
		}else if (windowsShellCodepage != null){
			encoding = windowsShellCodepage;
		}
		if (customTimeout > 15000){
			customTimeout = 15000;
		}else if (customTimeout <= 0){
			customTimeout = 5000;
		}
		Process process;
		try{
			//process = Runtime.getRuntime().exec(command); 	//old way
			ProcessBuilder builder = new ProcessBuilder();
			String[] osPart;
			if (isWindows()){
				osPart = new String[]{"cmd.exe", "/c"};
			}else{
				osPart = new String[]{"sh", "-c"};
			}
			builder.command(Stream.of(osPart, command).flatMap(Stream::of).toArray(String[]::new)); 	//tricky way to concatenate arrays
			//builder.directory(new File(System.getProperty("user.home"))); 	//set process directory or other options ...
			process = builder.start();
			
			//wait for finish or timeout
			if(!process.waitFor(customTimeout, TimeUnit.MILLISECONDS)){
			    //timeout - kill the process. 
				process.destroy(); // consider using destroyForcibly instead
			    return new RuntimeResult(3, null, new RuntimeException("Timeout!"));
			}
		}catch (Exception e){
			return new RuntimeResult(1, null, e);
		}
		List<String> output = null;
		try{
			output = FilesAndStreams.getLinesFromStream(process.getInputStream(), encoding);
			return new RuntimeResult(process.exitValue(), output, null);
		}catch(Exception e){
			return new RuntimeResult(1, output, e);
		}
	}

}
