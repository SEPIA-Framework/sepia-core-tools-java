package net.b07z.sepia.server.core.tools;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Connectors to simplify calling other APIs like HTTP GET etc.
 *  
 * @author Florian Quirin
 *
 */
public class Connectors {
	
	public static final String USER_AGENT = "Mozilla/5.0";
	public static final String HTTP_REST_SUCCESS = "HTTP_REST_SUCCESS";
	
	public static final int CONNECT_TIMEOUT = 15000;
	public static final int READ_TIMEOUT = 60000;
	
	public static final String HEADER_AUTHORIZATION = "Authorization";	//Authorization: <type> <credentials>, with type e.g.: Basic (base64(uname:pwd)), Bearer
	public static final String HEADER_ACCEPT_CONTENT = "Accept";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
	public static final String HEADER_USER_AGENT = "User-Agent";
	
	public enum Method {
		get,
		post,
		put,
		delete,
		head
	}
	/**
	 * Holds the result of an HTTP call (usually but not exclusively done with Apache {@link CloseableHttpClient}).
	 */
	public static class HttpClientResult {
		public int statusCode = 0;
		public String statusLine = "";
		public String content = "";
		public Charset encoding = null;
		public  Map<String, String> headers;
		
		HttpClientResult(String content, int statusCode){
			this.content = content;
			this.statusCode = statusCode;
		}
		HttpClientResult(String content, int statusCode, String statusLine){
			this.content = content;
			this.statusCode = statusCode;
			this.statusLine = statusLine;
		}
		HttpClientResult(String content, int statusCode, String statusLine, Charset encoding){
			this.content = content;
			this.statusCode = statusCode;
			this.statusLine = statusLine;
			this.encoding = encoding;
		}
		HttpClientResult(String content, int statusCode, String statusLine, Map<String, String> headers, Charset encoding){
			this.content = content;
			this.statusCode = statusCode;
			this.statusLine = statusLine;
			this.headers = headers;
			this.encoding = encoding;
		}
	}

	/**
	 * Sends a GET and parses the reply as JSON. Other than {@code httpGET_JSON},
	 * this leaves the reply unmodified, i.e. it won't add {@code HTTP_REST_SUCCESS}.
	 * Throws a RuntimeException on fail.
	 */
	public static JSONObject simpleJsonGet(String url) {
		return simpleJsonGet(url, null);
	}
	/**
	 * Sends a GET and parses the reply as JSON. Other than {@code httpGET_JSON},
	 * this leaves the reply unmodified, i.e. it won't add {@code HTTP_REST_SUCCESS}.
	 * Throws a RuntimeException on fail.
	 */
	public static JSONObject simpleJsonGet(String url, Map<String, String> headers) {
		try {
			URL urlObj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setConnectTimeout(CONNECT_TIMEOUT);
			con.setReadTimeout(READ_TIMEOUT);
			//con.setRequestProperty("content-type", "text/html");
			if (headers != null){
				for (Map.Entry<String, String> entry : headers.entrySet()){
					con.setRequestProperty(entry.getKey(), entry.getValue());
					//System.out.println(entry.getKey() +": "+ entry.getValue());
				}
			}
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK){
				try (InputStream stream = con.getInputStream()) {
					String content = getStreamContentAsString(con, stream);
					return JSON.parseStringOrFail(content);
				}
			} else {
				throw new RuntimeException("Could not get '" + url + "': response code " + responseCode);
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not get '" + url + "', error: " + e.getMessage(), e);
		}
	}
	/**
	 * Sends a GET and returns result as string. NOTE: sets content-type to: 'text/html'.
	 * Throws a RuntimeException on fail.
	 */
	public static String simpleHtmlGet(String url) {
		try {
			URL urlObj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept", "text/html");
			con.setConnectTimeout(CONNECT_TIMEOUT);
			con.setReadTimeout(READ_TIMEOUT);
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK){
				try (InputStream stream = con.getInputStream()) {
					String content = getStreamContentAsString(con, stream);
					return content;
				}
			} else {
				throw new RuntimeException(DateTime.getLogDate() + " ERROR - Could not get '" + url + "': response code " + responseCode);
			}
		} catch (Exception e) {
			throw new RuntimeException(DateTime.getLogDate() + " ERROR - Could not get '" + url + "', error: " + e.getMessage(), e);
		}
	}
	/**
	 * Simple HTTP GET. Basically the same as "simpleJsonGet" just realized with Apache HTTP client.
	 * Uses UTF-8 encoding and 'application/json' content-type.
	 * @param url - URL to call
	 * @return JSONObject
	 */
	public static JSONObject apacheHttpGETjson(String url) throws Exception{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Accept", "application/json");
		httpGet.addHeader("User-Agent", USER_AGENT);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		try {
		    //System.out.println(response.getStatusLine());
		    HttpEntity resEntity = response.getEntity();
		    String responseData = null;
		    if (resEntity != null){
	        	responseData = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
	        }
		    EntityUtils.consume(resEntity);
		    return JSON.parseStringOrFail(responseData);
		    
		} finally {
		    response.close();
		}
	}
	/**
	 * Simple HTTP GET. Basically the same as "simpleHtmlGet" just realized with Apache HTTP client and allows to set
	 * or skip custom content-type. Probably the most reliable GET version in this package if you need a string in return.<br>
	 * NOTE: If the URL was redirected it will produce a ERROR log message and NOT follow the link.<br>
	 * NOTE2: Cookie management is disabled<br>
	 * NOTE3: Content encoding is read from HttpEntity and defaults to UTF-8
	 * @param url - URL to call
	 * @param contentType - (Accept header) null for 'auto' or e.g. 'application/json', 'application/xml' or 'application/rss+xml' etc.
	 * @return {@link HttpClientResult}
	 */
	public static HttpClientResult apacheHttpGET(String url, String contentType) throws Exception {
		Map<String, String> headers = new HashMap<>();
		if (Is.notNullOrEmpty(contentType)){
			headers.put("Accept", contentType);	
		}
		return apacheHttpGET(url, headers);
	}
	/**
	 * Simple HTTP GET. Basically the same as "simpleHtmlGet" just realized with Apache HTTP client and allows to set
	 * or skip custom content-type. Probably the most reliable GET version in this package if you need a string in return.<br>
	 * NOTE: If the URL was redirected it will produce a ERROR log message and NOT follow the link.<br>
	 * NOTE2: Cookie management is disabled<br>
	 * NOTE3: Content encoding is read from HttpEntity and defaults to UTF-8
	 * NOTE4: Timeouts are: 5s (socketTimeout) and 8s (connect, connectionRequest). Retry is 2.
	 * @param url - URL to call
	 * @param headers - request headers (or null), e.g. "Accept", "User-Agent", etc.
	 * @return {@link HttpClientResult}
	 */
	public static HttpClientResult apacheHttpGET(String url, Map<String, String> headers) throws Exception {
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(8000)
				.setConnectionRequestTimeout(8000)
				.setSocketTimeout(5000)
				.build();
		CloseableHttpClient httpclient = HttpClientBuilder.create()
				.setDefaultRequestConfig(config)
				.setRetryHandler(new HttpRequestRetryHandler(){
			        @Override
			        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			            return executionCount <= 2 ;
			        }
			    })
				.disableRedirectHandling()
				.disableCookieManagement()
				.setUserAgent(USER_AGENT) 		//NOTE: this is primarily to avoid calls to system.java.version in sandbox
				.build();
		return apacheHttpGET(url, httpclient, headers);
	}
	/**
	 * Apache HTTP client wit custom settings. Compare: {@link #apacheHttpGET(String, Map)}.<br>
	 * NOTE: If the URL was redirected it will produce a ERROR log message and NOT follow the link.<br>
	 * NOTE2: Cookie management is disabled<br>
	 * NOTE3: Content encoding is read from HttpEntity and defaults to UTF-8
	 * @param url - URL to call
	 * @param httpclient - custom {@link CloseableHttpClient}. Use this if you need to change default properties like redirect or timeout.
	 * @param headers - request headers (or null), e.g. "Accept", "User-Agent", etc.
	 * @return {@link HttpClientResult}
	 */
	public static HttpClientResult apacheHttpGET(String url, CloseableHttpClient httpclient, Map<String, String> headers) throws Exception {
		//CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		if (headers != null){
			for (Map.Entry<String, String> entry : headers.entrySet()){
				httpGet.setHeader(entry.getKey(), entry.getValue());
			}
		}
		if (headers == null || !headers.containsKey("User-Agent")){
			httpGet.addHeader("User-Agent", USER_AGENT);
		}
		String statusLine = "";
		int statusCode = 0;
		String responseData = null;
		Charset charset = null;
		try (CloseableHttpResponse response = httpclient.execute(httpGet);){
			statusLine = response.getStatusLine().toString();
			statusCode = response.getStatusLine().getStatusCode();
			//System.out.println(statusLine);
			if (statusCode == 301){
				String errorRedirect = response.getFirstHeader("Location").getValue();
				statusLine += (", NEW URI: " + errorRedirect);
    			Debugger.println("HTTP GET: '" + url + "' has REDIRECT to: " + errorRedirect, 1);
    		}else{
			    HttpEntity resEntity = response.getEntity();
			    if (resEntity != null){
			    	ContentType ct = ContentType.getOrDefault(resEntity);
			    	charset = ct.getCharset();
			    	if (charset == null){
			            charset = StandardCharsets.UTF_8;
			        }
		        	responseData = EntityUtils.toString(resEntity, charset);
		        }
			    EntityUtils.consume(resEntity);
    		}
			Map<String, String> responseHeaders = new HashMap<>();
			for (Header header : response.getAllHeaders()){
				responseHeaders.put(header.getName(), header.getValue());
			}
		    return new HttpClientResult(responseData, statusCode, statusLine, responseHeaders, charset);
		    
		}catch (Exception e){
			if (Is.nullOrEmpty(statusLine)){
				statusLine = e.getMessage();
			}
			return new HttpClientResult(null, statusCode, statusLine, charset);
		}
	}
	
	/**
	 * Apache HttpClient GET with no restrictions on the SSL certificate validity.
	 * @param url - call this URL
	 * @return {@link HttpClientResult}
	 */
	public static HttpClientResult httpGetSelfSignedSSL(String url) throws IOException, GeneralSecurityException {
		//Apache docs:
		//https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html
		//https://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html - 2.7.3 ...
		
		SSLContext sslContext = SSLContexts.custom()
			    .loadTrustMaterial(new TrustSelfSignedStrategy())
			    .build();
		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);		//optionally add: NoopHostnameVerifier.INSTANCE
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
			    .register("https", socketFactory)
			    .build();
		HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);        
		CloseableHttpClient httpClient = HttpClients.custom()
			    .setConnectionManager(cm)
			    .build();
		HttpUriRequest httpMethod = new HttpGet(url);
		
		CloseableHttpResponse sslResponse = httpClient.execute(httpMethod);
		int statusCode = sslResponse.getStatusLine().getStatusCode();
		String content = "";
		try {
		    HttpEntity entity = sslResponse.getEntity();
		    if (entity != null) {
		        //long len = entity.getContentLength();
		        //if (len != -1 && len < 2048) {
		        	content = EntityUtils.toString(entity);
		        //} else {
		            // Stream content out
		        //}
		    }
		} finally {
			sslResponse.close();
		}
		return new HttpClientResult(content, statusCode);
	}
	
	/**
	 * Make HTTP GET request to URL and get JSON response. Check with {@code httpSuccess(...)} for status.
	 * @param url - URL address to call including all parameters
	 * @return JSONObject response of URL call - Note:<br> 
	 * if response is not JSON it will be placed e.g. as "STRING" field in the result or "JSONARRAY" if it's an array. 
	 */
	public static JSONObject httpGET(String url) {
		return httpGET(url, null);
	}
	/**
	 * Make HTTP GET request to URL and get JSON response. Check with {@code httpSuccess(...)} for status.
	 * @param url - URL address to call including none or only some parameters
	 * @param params - additional parameters added to URL (use e.g. "?q=search_term" or "&type=json" etc.) or null
	 * @return JSONObject response of URL call - Note: if response is not JSON it will be placed e.g. as "STRING" field in the result or "JSONARRAY" if it's an array.
	 */
	public static JSONObject httpGET(String url, String[] params) {
		return httpGET(url, params, null);
	}
	/**
	 * Make HTTP GET request to URL and get JSON response. Check with {@code httpSuccess(...)} for status.
	 * @param url - URL address to call including none or only some parameters
	 * @param params - additional parameters added to URL (use e.g. "?q=search_term" or "&type=json" etc.) or null
	 * @param headers - Map with request properties (keys) and values. Sets only 'User-Agent' header by default.
	 * @return JSONObject response of URL call - Note: if response is not JSON it will be placed e.g. as "STRING" field in the result or "JSONARRAY" if it's an array.
	 */
	public static JSONObject httpGET(String url, String[] params, Map<String, String> headers) {
		return httpGET(url, params, headers, CONNECT_TIMEOUT);
	}
	/**
	 * Make HTTP GET request to URL and get JSON response. Check with {@code httpSuccess(...)} for status.
	 * @param url - URL address to call including none or only some parameters
	 * @param params - additional parameters added to URL (use e.g. "?q=search_term" or "&type=json" etc.) or null
	 * @param headers - Map with request properties (keys) and values. Sets only 'User-Agent' header by default.
	 * @param connectTimeout - max. time to wait for connection (ms)
	 * @return JSONObject response of URL call - Note: if response is not JSON it will be placed e.g. as "STRING" field in the result or "JSONARRAY" if it's an array.
	 */
	public static JSONObject httpGET(String url, String[] params, Map<String, String> headers, int connectTimeout) {
		int responseCode = -1;
		String success_str = HTTP_REST_SUCCESS;
		try{
			if (params != null){
				for (String s : params){
					url = url + s;
				}
			}
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			//con.setRequestProperty(HEADER_ACCEPT_ENCODING, "gzip");		//TODO: make this common?
			con.setConnectTimeout(connectTimeout);
			con.setReadTimeout(READ_TIMEOUT);
			
			if (headers != null){
				for (Map.Entry<String, String> entry : headers.entrySet()){
					con.setRequestProperty(entry.getKey(), entry.getValue());
					//System.out.println(entry.getKey() +": "+ entry.getValue());
				}
			}
	
			responseCode = con.getResponseCode();
			//System.out.println("GET Response Code : " + responseCode);		//debug
	
			//success?
			if (responseCode >= 200 && responseCode < 300){		//(responseCode == HttpURLConnection.HTTP_OK){
				/*
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	 
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				//result
				//System.out.println(response.toString());						//debug
				String res = response.toString();
				JSONObject result = build(res, success_str);
				return result;
				*/
				String content = getStreamContentAsString(con, con.getInputStream());
				JSONObject result = build(content, success_str);
				return result;
		
			}else{
				//result
				//System.out.println("GET request did not work");				//debug
				JSONObject json = new JSONObject();
				JSON.add(json, success_str, false);
				JSON.add(json, "code", responseCode);
				return json;
			}
			
		}catch (Exception e){
			//result
			//System.out.println("GET request did not work");					//debug
			JSONObject json = new JSONObject();
			JSON.add(json, success_str, false);
			JSON.add(json, "error", e.toString());
			JSON.add(json, "code", responseCode);
			return json;
		}
	}
	
	//--------------------------POST--------------------------------
	
	/**
	 * Make a HTTP POST request to targetUrl with x-www-form-urlencoded parameters. Check {@code httpSuccess(...)} for status.
	 * @param targetURL - URL of service
	 * @param urlParameters - parameters for x-www-form-urlencoded content-type, e.g. "a=1&b=2&c=3..." 
	 * @return server answer as JSONObject
	 */
	public static JSONObject httpFormPOST(String targetURL, String urlParameters){
		
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Content-Length", Integer.toString(urlParameters.getBytes().length));
		headers.put("Content-Language", "en-US");
			
		return httpPOST(targetURL, urlParameters, headers);
	}
	/**
	 * Make a HTTP POST request to targetUrl with custom headers. Check {@code httpSuccess(...)} for status.
	 * @param targetURL - URL of service
	 * @param data - data in chosen content-type, e.g. url parameter style or JSON string
	 * @param headers - HashMap with request properties (keys) and values. Set by default if null: 'Content-Type' = 'application/json'.
	 * @return JSONObject with response
	 */
	public static JSONObject httpPOST(String targetURL, String data, Map<String, String> headers){
		return httpPOST(targetURL, data, headers, CONNECT_TIMEOUT);
	}
	/**
	 * Make a HTTP POST request to targetUrl with custom headers. Check {@code httpSuccess(...)} for status.
	 * @param targetURL - URL of service
	 * @param data - data in chosen content-type, e.g. url parameter style or JSON string
	 * @param headers - HashMap with request properties (keys) and values. Set by default if null: 'Content-Type' = 'application/json'.
	 * @param connectTimeout - max. time to wait for connection (ms)
	 * @return JSONObject with response
	 */
	public static JSONObject httpPOST(String targetURL, String data, Map<String, String> headers, int connectTimeout){
		URL url;
		HttpURLConnection connection = null;
		int responseCode = -1;
		String success_str = HTTP_REST_SUCCESS;
		try {
			
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(READ_TIMEOUT);
			//System.out.println("---headers---");
			if (headers == null){
				//headers
				headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/json");
				headers.put("Content-Length", Integer.toString(data.getBytes().length));
			}
			for (Map.Entry<String, String> entry : headers.entrySet()){
				connection.setRequestProperty(entry.getKey(), entry.getValue());
				//System.out.println(entry.getKey() +": "+ entry.getValue());
			}			
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			//Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(data.getBytes(StandardCharsets.UTF_8));
			//wr.writeBytes(data);
			wr.flush();
			wr.close();
			
			responseCode = connection.getResponseCode();
			//System.out.println("POST Response Code : " + responseCode);		//debug

			//Get Response
			InputStream is;
			boolean success = false;
			if (responseCode >= 200 && responseCode < 300){		//(responseCode == HttpURLConnection.HTTP_OK){
				success = true;
				is = connection.getInputStream();
			}else{
				is = connection.getErrorStream();
			}
			/*
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				//response.append('\r');	//line break messes it all up
			}
			rd.close();
			String res = response.toString();
			*/
			String res = getStreamContentAsString(connection, is);
			
			if (success){
				JSONObject result = build(res, success_str);
				return result;
			}else{
				JSONObject json = new JSONObject();
				JSON.add(json, success_str, false);
				JSON.add(json, "code", responseCode);
				JSON.add(json, "error", res);
				return json;
			}

	    }catch (Exception e) {
	    	JSONObject json = new JSONObject();
			JSON.add(json, success_str, false);
			JSON.add(json, "code", responseCode);
			JSON.add(json, "error", e.toString());
			return json;

	    }finally {
	    	if(connection != null) {
	    		connection.disconnect(); 
	    	}
	    }
	}
	
	//----------------------PUT-------------------------
	
	/**
	 * Make a HTTP PUT request to targetUrl with custom headers. Check {@code httpSuccess(...)} for status.
	 * @param targetURL - URL of service
	 * @param data - data in chosen content-type, e.g. url parameter style or JSON string
	 * @param headers - HashMap with request properties (keys) and values. NO headers set by default if null.
	 * @return JSONObject with response
	 */
	public static JSONObject httpPUT(String targetURL, String data, Map<String, String> headers) {
		URL url;
		HttpURLConnection connection = null;
		int responseCode = -1;
		String success_str = HTTP_REST_SUCCESS;
		try {
			
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("PUT");
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			//System.out.println("---headers---");
			for (Map.Entry<String, String> entry : headers.entrySet()){
				connection.setRequestProperty(entry.getKey(), entry.getValue());
				//System.out.println(entry.getKey() +": "+ entry.getValue());
			}			
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			//Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(data.getBytes(StandardCharsets.UTF_8));
			//wr.writeBytes(data);
			wr.flush();
			wr.close();
			
			responseCode = connection.getResponseCode();
			//System.out.println("POST Response Code : " + responseCode);		//debug

			//Get Response
			InputStream is;
			boolean success = false;
			if (responseCode >= 200 && responseCode < 300){		//(responseCode == HttpURLConnection.HTTP_OK){
				success = true;
				is = connection.getInputStream();
			}else{
				is = connection.getErrorStream();
			}
			String res = getStreamContentAsString(connection, is);
			
			if (success){
				JSONObject result = build(res, success_str);
				return result;
				
			}else{
				JSONObject json = new JSONObject();
				JSON.add(json, success_str, false);
				JSON.add(json, "code", responseCode);
				JSON.add(json, "error", res);
				return json;
			}

	    }catch (Exception e) {
	    	JSONObject json = new JSONObject();
			JSON.add(json, success_str, false);
			JSON.add(json, "code", responseCode);
			JSON.add(json, "error", e.toString());
			return json;

	    }finally {
	    	if(connection != null) {
	    		connection.disconnect(); 
	    	}
	    }
	}
	
	//-------------DELETE--------------
	
	/**
	 * Make HTTP DELETE request to URL and get JSON response.. Use {@code httpSuccess(...)} for status.
	 * @param url - URL address to call including none or only some parameters/paths
	 * @return
	 */
	public static JSONObject httpDELETE(String url) {
		return httpDELETE(url, null);
	}
	/**
	 * Make HTTP DELETE request to URL and get JSON response.. Use {@code httpSuccess(...)} for status.
	 * @param url - URL address to call including none or only some parameters/paths
	 * @param headers - Map with request properties (keys) and values. Sets only 'User-Agent' header by default.
	 * @return
	 */
	public static JSONObject httpDELETE(String url, Map<String, String> headers) {
		int responseCode = -1;
		String success_str = HTTP_REST_SUCCESS;
		try{
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			con.setRequestMethod("DELETE");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setConnectTimeout(CONNECT_TIMEOUT);
			con.setReadTimeout(READ_TIMEOUT);
			
			if (headers != null){
				for (Map.Entry<String, String> entry : headers.entrySet()){
					con.setRequestProperty(entry.getKey(), entry.getValue());
					//System.out.println(entry.getKey() +": "+ entry.getValue());
				}
			}
	
			responseCode = con.getResponseCode();
			//System.out.println("GET Response Code : " + responseCode);		//debug
	
			//success?
			if (responseCode >= 200 && responseCode < 300){		//(responseCode == HttpURLConnection.HTTP_OK){
				String res = getStreamContentAsString(con, con.getInputStream());
				JSONObject result = build(res, success_str);
				return result;
		
			}else{
				//result
				//System.out.println("GET request did not work");				//debug
				JSONObject json = new JSONObject();
				JSON.add(json, success_str, false);
				JSON.add(json, "code", responseCode);
				return json;
			}
			
		}catch (Exception e){
			//result
			//System.out.println("GET request did not work");					//debug
			JSONObject json = new JSONObject();
			JSON.add(json, success_str, false);
			JSON.add(json, "error", e.toString());
			JSON.add(json, "code", responseCode);
			return json;
		}
	}
	
	//-------------- COMMON ------------------
	
	/**
	 * Add 'Authorization' header to given headers. (NOTE: this will modify the given object!)
	 * @param headers - given headers map or null (will create one)
	 * @param authType - types like 'Basic' or 'Bearer'
	 * @param authData - data as string, e.g. for type 'Basic' this should be a base64 coded string of "username:password"
	 * @return modified header map
	 */
	public static Map<String, String> addAuthHeader(Map<String, String> headers, String authType, String authData){
		if (headers == null){
			headers = new HashMap<>();
		}
		headers.put(Connectors.HEADER_AUTHORIZATION, authType.trim() + " " + authData);
		return headers;
	}
	
	/**
	 * Convenience method to check if the HTTP REST call was successful.<br>
	 * The HTTP REST methods used here add a helper field (HTTP_REST_SUCCESS) to the result to track the state of the call.
	 * With this method you can check if this field exists and says "true".
	 */
	public static boolean httpSuccess(JSONObject response){
		return httpSuccess(response, false);
	}
	/**
	 * Convenience method to check if the HTTP REST call was successful.<br>
	 * The HTTP REST methods used here add a helper field (HTTP_REST_SUCCESS) to the result to track the state of the call.
	 * With this method you can check if this field exists and says "true".
	 * @param response - HTTP response
	 * @param removeTag - if response contains HTTP_REST_SUCCESS field remove it after test  
	 */
	public static boolean httpSuccess(JSONObject response, boolean removeTag){
		if (response == null){
			return false;
		}
		Object restO = response.get(HTTP_REST_SUCCESS);
		boolean rest = (restO == null)? false : (boolean) restO;
		if (removeTag){
			response.remove(HTTP_REST_SUCCESS);
		}
		return rest;
	}
	
	/**
	 * Get a readable string of the submitted error if the request was not successful.
	 */
	public static String httpError(JSONObject response){
		return ("code: " + response.get("code") + ", error: " + response.get("error"));
	}
	
	/**
	 * Is response GZIP compressed?
	 */
	private static boolean isGzipResponse(HttpURLConnection con){
	    String encodingHeader = con.getHeaderField("Content-Encoding");
	    return (encodingHeader != null && encodingHeader.toLowerCase().contains("gzip"));
	}
	
	/**
	 * Get content of stream as string.
	 */
	private static String getStreamContentAsString(HttpURLConnection con, InputStream is) throws IOException {
		if (isGzipResponse(con)){
			InputStreamReader isr = new InputStreamReader(new GZIPInputStream(is), StandardCharsets.UTF_8);
			return IOUtils.toString(isr);
		}else{
			InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
			return IOUtils.toString(isr);
		}
	}
	
	/**
	 * Build result depending on type of reply (JSON string, JSON array, simple string).
	 * Adds the "success" tag as well.
	 * @param res - result received from HTTP connection as string
	 * @param successTag - field to add indicating the success
	 */
	private static JSONObject build(String res, String successTag){
		//System.out.println(res);						//debug
		if (Is.notNullOrEmpty(res)){
			res = (res.charAt(0) == '\uFEFF')? res.substring(1) : res;
			res = res.replaceAll("^(\\r+|\\n+|\\t+)", "").trim(); 		//NOTE: this might have consequences for text formatting
		}
		//debug:
		/*
		System.out.println(res.substring(0, 1));
		for (char c : res.toCharArray()) {
		    System.out.printf("U+%04x ", (int) c);
		    break;
		}
		*/
		JSONObject result;
		try{
			if (res.startsWith("{")){
				//parse JSONObject
				JSONParser parser = new JSONParser();
				result = (JSONObject) parser.parse(res);
				JSON.add(result, successTag, true);
			}else if (res.startsWith("[{")){
				//parse JSONArray
				JSONParser parser = new JSONParser();
				JSONArray arr = (JSONArray) parser.parse(res);
				result = new JSONObject();
				JSON.add(result, "JSONARRAY", arr);
				JSON.add(result, successTag, true);
			}else{
				//save String only
				result = new JSONObject();
				JSON.add(result, "STRING", res);
				JSON.add(result, successTag, true);
			}
			//System.out.println(result.toString());						//debug
			return result;
		}catch (ParseException e){
			System.err.println(DateTime.getLogDate() + " ERROR - Connectors.java / build() - Failed to parse JSON string: " + res);
			result = new JSONObject();
			JSON.add(result, successTag, false);
			JSON.add(result, "error", "result could not be parsed");
			JSON.add(result, "code", "500");
			return result;
		}
	}

}
