package tools;

import net.b07z.sepia.server.core.tools.RuntimeInterface;
import net.b07z.sepia.server.core.tools.RuntimeInterface.RuntimeResult;

public class Test_RuntimeInterface {

	public static void main(String[] args){
		//test Windows variables
		String argStr = "set /p test=KO %HOMEPATH% $HOME %FAKE% $FAKE &()[]$% & ^ 2^5 ( ) [ ] $ % $(pwd) %test%";
		System.out.println("Expect: " + "[\"" + argStr + "\"]");
		String[] cmd = new String[]{"echo", argStr};
		String res = call(cmd, false);
		if (!res.equals("[\"" + argStr + "\"]")){
			System.err.println("Result 1 is not same: " + res);
		}
		res = call(cmd, true);
		if (!res.equals("[\"" + argStr + "\"]")){
			System.err.println("Result 2 is not same: " + res);
		}
		cmd = new String[]{"if", "exist", "LICENSE", "echo", "OK"};
		res = call(cmd, false);
		System.out.println("out: " + res);
	}

	private static String call(String[] cmd, boolean restric){
		RuntimeResult rtr = RuntimeInterface.runCommand(cmd, 10000, restric);
		int code = rtr.getStatusCode();
		System.out.println("status code: " + code);
		if (code == 0){
			System.out.println("output: " + rtr.getOutput().toString());
			return rtr.getOutput().toString();
		}else{
			System.out.println("error: " +  rtr.getException().getMessage());
			return "";
		}
	}
}
