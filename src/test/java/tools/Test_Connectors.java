package tools;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.tools.Connectors;
import net.b07z.sepia.server.core.tools.JSON;

public class Test_Connectors {

	public static void main(String[] args){
		
		String url = "http://localhost:8073/fhem?cmd=jsonlist2&XHR=1&fwcsrf=csrf_150228387432057";  //FHEM demo with Nginx proxy to force GZIP
		Map<String, String> headers = new HashMap<>();
		headers.put(Connectors.HEADER_ACCEPT_CONTENT, "application/json");
		headers.put(Connectors.HEADER_ACCEPT_ENCODING, "gzip");
		JSONObject response = Connectors.httpGET(url, null, headers);
		JSON.prettyPrint(response);
	}

}
