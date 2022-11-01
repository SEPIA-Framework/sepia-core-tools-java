package net.b07z.sepia.server.core.server;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.options;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.servlet.ServletOutputStream;

import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.tools.Debugger;
import net.b07z.sepia.server.core.tools.FilesAndStreams;
import net.b07z.sepia.server.core.tools.Is;
import net.b07z.sepia.server.core.tools.JSON;
import net.b07z.sepia.server.core.tools.Security;
import spark.Request;
import spark.Response;

/**
 * Tools and setup-methods for Spark Java web-Services framework.
 * 
 * @author Florian Quirin
 *
 */
public class SparkJavaFw {

	/**
	 * Enable CORS aka set access-control headers. This method is an initialization method and should be called once.
	 */
	public static void enableCORS(final String origin, final String methods, final String headers) {
	
	    options("/*", (request, response) -> {
	
	        String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
	        if (accessControlRequestHeaders != null) {
	            response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
	        }
	
	        String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
	        if (accessControlRequestMethod != null) {
	            response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
	        }
	
	        return "OK";
	    });
	
	    before((request, response) -> {
	        response.header("Access-Control-Allow-Origin", origin);
	        response.header("Access-Control-Request-Method", methods);
	        response.header("Access-Control-Allow-Headers", headers);
	    });
	}
	public static void enableFileCORS(final String origin, final String methods, final String headers){
		staticFiles.header("Access-Control-Allow-Origin", origin);
		staticFiles.header("Access-Control-Request-Method", methods);
		staticFiles.header("Access-Control-Allow-Headers", headers);
	}
	
	/**
	 * Handle unexpected errors 
	 */
	public static void handleError(){
		exception(Exception.class, (ex, request, response) -> {
			Debugger.println("Exception for request to " + request.url() + ": " + ex, 1);
			//print the last 3 traces ... the rest is typically server spam ^^
			Debugger.printStackTrace(ex, 3);
			JSONObject result = new JSONObject();
			JSON.add(result, "result", "fail");
			JSON.add(result, "error", "500 internal error");
			JSON.add(result, "info", ex.getMessage());
			response.body(returnResult(request, response, result.toJSONString(), 200)); 	//code 500 always creates client timeout :/ TODO: check again
		});
	}
	
	/**
	 * Redirect to new path.
	 */
	public static String redirect(Request request, Response response, String redirectPath){
		//response.status(301);
		response.redirect(redirectPath);
		return null;
	}

	/**
	 * Return result as requested content (plain-text, javascript, json, etc...)
	 * @param request - request containing the header (content-type)
	 * @param response - expected response (passed down from main)
	 * @param msg - message to return (text, json string, ...)
	 * @param statusCode - HTTP result code (200,401,...)
	 * return proper result string
	 */
	public static String returnResult(Request request, Response response, String msg, int statusCode){
		//get content header
		String header = request.headers("Content-type");
		//System.out.println("Content-type: " + header);
		if (header == null){
			header = request.headers("Accept");
			if (header == null){
				header = "";
			}
		}
		//System.out.println(header);		//debug
		//System.out.println(request.queryString().length());		//content length
		
		//return answer in requested format
		response.status(statusCode);
		if (header.contains("application/javascript")){
			response.type("application/javascript");
			msg = request.queryParams("callback") + "(" + msg + ");";
		}else if (header.contains("text/")){
			if (msg.startsWith("{") || msg.startsWith("[")){
				response.type("text/plain; charset=utf-8");
			}else{
				response.type("text/html; charset=utf-8");
			}
		}else if (header.matches(".*/json($|;| ).*")){
			response.type("application/json");
		}else if (header.contains("x-www-form-urlencoded")){
			response.type("application/json");
		}else if (header.contains("multipart/form-data")){
			if (msg.startsWith("{") || msg.startsWith("[")){
				response.type("text/plain; charset=utf-8");
			}else{
				response.type("text/html; charset=utf-8");
			}
		}else{
			//try jsonp
			String callback = request.queryParams("callback");
			if (callback != null && !callback.isEmpty()){
				response.type("application/javascript");
				msg = request.queryParams("callback") + "(" + msg + ");";
			//fallback to plain text
			}else{
				response.type("text/plain; charset=utf-8");
			}
		}
		return msg;
	}
	
	/**
	 * Return simple success message.
	 */
	public static String sendSuccessResponse(Request request, Response response) {
		JSONObject msg = new JSONObject();
		JSON.add(msg, "result", "success");
		return returnResult(request, response, msg.toJSONString(), 200);
	}

	/**
	 * Return simple access-restricted message.
	 */
	public static String returnNoAccess(Request request, Response response){
		/*
		response.status(401);
		response.type("text/plain");
		return "401 Unauthorized";
		*/
		String msg = "{\"result\":\"fail\",\"error\":\"401 not authorized\"}";
		return returnResult(request, response, msg, 200); 		//TODO: we should change this code to 401 as well
	}

	/**
	 * Return simple access-restricted message with custom error code.
	 * @param errorCode - code returned from authenticator - 1: communication error, 2: access denied, ...
	 * @return
	 */
	public static String returnNoAccess(Request request, Response response, int errorCode){
		if (errorCode == 2){
			return returnNoAccess(request, response);
		}else{
			String msg = "{\"result\":\"fail\",\"error\":\"400 or 500 bad request or communication error\",\"code\":\"" + errorCode + "\"}";
			return returnResult(request, response, msg, 200); 		//TODO: we should change this code to 400/500 as well
		}
	}
	
	/**
	 * Return simple "path invalid" message.
	 */
	public static String returnPathNotFound(Request request, Response response){
		return returnResult(request, response, JSON.make(
				"result", "fail",
				"error", "404 path invalid"
		).toJSONString(), 404);
	}
	
	/**
	 * Return simple "internal error" message.
	 */
	public static String returnInternalError(Request request, Response response){
		return returnResult(request, response, JSON.make(
				"result", "fail",
				"error", "500 internal error"
		).toJSONString(), 500);
	}
	
	/**
	 * Return simple "internal error" message.
	 */
	public static String returnNotAcceptable(Request request, Response response){
		return returnResult(request, response, JSON.make(
				"result", "fail",
				"error", "406 not acceptable"
		).toJSONString(), 406);
	}

	/**
	 * Get request body or throw error.
	 */
	public static JSONObject getRequestBody(Request request){
		String body = request.body();
		if (body != null && !body.isEmpty()){
			return JSON.parseStringOrFail(body);
		}else{
			throw new RuntimeException("Request body is null!");
		}
	}
	
	/**
	 * Get parameter from request body (e.g. POST request).
	 * @param requestBody
	 * @param parameterName
	 * @return string or null
	 */
	public static String getParameter(JSONObject requestBody, String parameterName){
		return JSON.getString(requestBody, parameterName);
	}
	/**
	 * Get parameter from request (e.g. GET request).
	 * @param request
	 * @param parameterName
	 * @return string or null
	 */
	public static String getParameter(Request request, String parameterName){
		return request.queryParams(parameterName);
	}

	/**
	 * Check if the request is coming from the private network.
	 */
	public static boolean requestFromPrivateNetwork(Request request){
		String host = request.host();
		boolean isPrivate;
		try{
			isPrivate = Security.isPrivateNetwork(host);
		}catch (Exception e){
			isPrivate = false;
			Debugger.printStackTrace(e, 3);
		}
		if (!isPrivate){
			Debugger.println("Unauthorized access attempt to server config! Host: " + host, 1);
			return false;
		}else{
			return true;
		}
	}
	
	//---------- File Stream -----------
	
	public static void streamFile(Request req, Response res, File file, String mimeType, int maxChunkSize) 
			throws FileNotFoundException, IOException {
		String range = req.headers("Range");
		int[] fromToLength = fromTo(file, range, maxChunkSize);
		int length = fromToLength[2];
		byte[] bytes = FilesAndStreams.readByteRange(file.getPath(), fromToLength[0], length);

		res.header("Accept-Ranges", "bytes"); 
		res.header("Content-Range", "bytes " + fromToLength[0] + "-" + fromToLength[1] + "/" + fromToLength[3]);
		res.header("Content-Length", String.valueOf(length));
		//res.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
		//res.header("Connection", "Keep-Alive");
		//res.header("Keep-Alive", "timeout=15, max=100");
		//res.header("Etag", Security.bytearrayToHexString(...getMD5(bytes));
		res.header("Cache-Control", "no-cache, private");
		res.header("Expires", "0");
		res.header("Content-Transfer-Encoding", "binary");
		res.header("Transfer-Encoding", "chunked");
		res.type(mimeType);
		res.status(206);
		
		try (final ServletOutputStream os = res.raw().getOutputStream()){
			os.write(bytes);
			os.close();
		}
	}
	private static int[] fromTo(File file, String range, int maxChunkSize){
		int[] data = new int[4];
		int fileLength = (int) file.length();
		int from;
		int to;
		int length;
		if (Is.nullOrEmpty(range)){
			//serve all
			from = 0;
			to = fileLength - 1;
			length = fileLength;
		}else{
			String[] ranges = range.split("=")[1].split("-");
			from = Integer.parseInt(ranges[0]);
			if (ranges.length == 2){
				to = Math.min(fileLength - 1, Integer.parseInt(ranges[1]));
			}else{
				int fallbackChunkSize = Math.max(153600, Math.min(maxChunkSize, Math.round(fileLength/10f)));
				to = Math.min(fileLength - 1, from + fallbackChunkSize);
			}
			length = to - from + 1;
		}
		data[0] = from;			//start
		data[1] = to;			//stop
		data[2] = length;		//bytes length
		data[3] = fileLength;	//full file length
		return data;
	}
}
