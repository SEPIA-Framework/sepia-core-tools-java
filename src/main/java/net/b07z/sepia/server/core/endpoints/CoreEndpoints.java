package net.b07z.sepia.server.core.endpoints;

import java.io.File;
import java.util.List;
import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.server.BasicStatistics;
import net.b07z.sepia.server.core.server.ConfigDefaults;
import net.b07z.sepia.server.core.server.RequestGetOrFormParameters;
import net.b07z.sepia.server.core.server.RequestParameters;
import net.b07z.sepia.server.core.server.SparkJavaFw;
import net.b07z.sepia.server.core.server.Validate;
import net.b07z.sepia.server.core.tools.FilesAndStreams;
import net.b07z.sepia.server.core.tools.JSON;
import spark.Request;
import spark.Response;

/**
 * A couple of endpoints that are useful for every server.
 *   
 * @author Florian Quirin
 *
 */
public class CoreEndpoints {

	/**
	 * ---ONLINE CHECK---<br>
	 * End-point to check if the server can be reached. Returns empty answer.<br>
	 * Note: this is a GET endpoint
	 */
	public static String onlineCheck(Request request, Response response){
		//stats
		BasicStatistics.addOtherApiHit("Online endpoint");
		BasicStatistics.addOtherApiTime("Online endpoint", 1);
		
		return SparkJavaFw.returnResult(request, response, "", 204);
	}

	/**
	 * ---PING SERVER---<br>
	 * Ping server to see if its online.<br>
	 * Note: this is a GET endpoint
	 */
	public static String ping(Request request, Response response, String serverName){
		long tic = System.currentTimeMillis();
		
		JSONObject msgJSON = JSON.make("result", "success", "server", serverName);
		
		//stats
		BasicStatistics.addOtherApiHit("Server 'ping' endpoint");
		BasicStatistics.addOtherApiTime("Server 'ping' endpoint", tic);
		
		return SparkJavaFw.returnResult(request, response, msgJSON.toJSONString(), 200);
	}

	/**
	 * ---VALIDATE SERVER---<br>
	 * End-point to ping the server and get back some basic info like API version 
	 * and signature (calculated from local_name, local_secret and challenge).<br>
	 * Note: this is a GET endpoint
	 */
	public static String validateServer(Request request, Response response, 
			String serverName, String apiVersion, String localName, String localSecret){
		//prepare parameters
		long tic = System.currentTimeMillis();
		RequestParameters params = new RequestGetOrFormParameters(request);
					
		long t = System.currentTimeMillis();
		JSONObject msg = new JSONObject();
		JSON.add(msg, "result", "success");
		JSON.add(msg, "server", serverName);
		JSON.add(msg, "version", apiVersion);
		JSON.add(msg, "privacy_policy", ConfigDefaults.privacyPolicyLink);
		JSON.add(msg, "host", request.host());
		JSON.add(msg, "url", request.url());
		JSON.add(msg, "time", t);
		JSON.add(msg, "signature", Validate.getLocalSignature(request, params.getString("challenge"), t, localName, localSecret));
		
		//stats
		BasicStatistics.addOtherApiHit("Server 'validation' endpoint");
		BasicStatistics.addOtherApiTime("Server 'validation' endpoint", tic);
	
		return SparkJavaFw.returnResult(request, response, msg.toJSONString(), 200);
	}
	
	/**
	 * ---LIST CONTENT---<br>
	 * Get content of web-server folder as HTML index file (list of links) if allowed.<br>
	 * NOTE: will replace "endpointPath" to reconstruct correct web-server path.
	 * @return
	 */
	public static String getWebContentIndex(Request request, Response response, String baseFolderPath, String endpointPath, boolean isAllowed){
		if (!isAllowed){
			return SparkJavaFw.returnResult(request, response, JSON.make(
					"result", "fail", 
					"error", "Not supported by this server"
			).toJSONString(), 403);
		}
		//path
		String[] pathArray = request.splat();
		String path = "";
		if (pathArray.length > 0){
			path = pathArray[0].replaceFirst("/$", "").trim();
		}
		String fullPath = (baseFolderPath + "/" + path);
		List<File> files = FilesAndStreams.directoryToFileList(fullPath, null, false);
		if (files == null){
			return SparkJavaFw.returnPathNotFound(request, response);
		}
		String htmlResponse = "";
		for (File f : files){
			String fName = f.getName();
			if (fName.equalsIgnoreCase("no-index")){
				isAllowed = false;
				break;
			}else{
				//htmlResponse += ("<li><a href='" + fName + "'>" + fName + "</a></li>");
				htmlResponse += ("<button>" + fName + "</button>");
			}
		}
		if (!isAllowed){
			return SparkJavaFw.returnResult(request, response, JSON.make(
					"result", "fail", 
					"error", "Not allowed for this folder"
			).toJSONString(), 403);
		}else{
			htmlResponse = "<!DOCTYPE html><html>"
					+ "<head>"
						+ "<meta name='viewport' content='width=device-width, initial-scale=1'>"
						+ "<title>SEPIA server files</title>"
						+ "<style>"
							+ "* { font-family: sans-serif; } "
							+ "::-moz-focus-inner { border:0; } "
							+ "body { background: #111; color: #eee; } "
							+ "button { "
								+ "border: 1px solid #333; background: #222; color: #eee; padding: 8px; margin: 4px; "
								+ "cursor: pointer; font-size: medium; transition: background .5s; "
							+ "} "
							+ "button:hover { background: #2e2e2e; } "
						+ "</style>"
					+ "</head><body>"
					+ "<h3>Files of folder '" + path + "':</h3>"
					+ "<div style='display: flex; flex-direction: column;'>" + htmlResponse + "</div>"
					+ "<script>"
						+ "var absPath=location.href.replace('" + endpointPath + "', '/') + '/'; "
						+ "absPath = absPath.replace(/\\/\\/$/, '/'); "
						+ "var refs = document.getElementsByTagName('button'); "
						+ "for(var i=0; i<refs.length; i++){ "
							+ "(function(){ "
								+ "var link = absPath + refs[i].innerHTML; "
								+ "refs[i].onclick = function(){ window.location.assign(link); }; "
								+ "refs[i].dataset.url = link; "
							+ "})(); "
						+ "} "
					+ "</script>"
					/*
					+ "<ul>" + htmlResponse + "</ul>"
					+ "<script>"
						+ "var absPath=location.href.replace('" + endpointPath + "', '/') + '/'; "
						+ "absPath = absPath.replace(/\\/\\/$/, '/'); "
						+ "var refs = document.getElementsByTagName('a'); "
						+ "for(var i=0; i<refs.length; i++){ refs[i].href = absPath + refs[i].innerHTML; } "
					+ "</script>"
					*/
					+ "</body></html>";
			return htmlResponse;
		}
	}
	
}
