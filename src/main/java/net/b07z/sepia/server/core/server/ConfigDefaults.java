package net.b07z.sepia.server.core.server;

import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.microservices.DbStationResults;
import net.b07z.sepia.server.core.tools.Connectors;
import net.b07z.sepia.server.core.tools.ContentBuilder;
import net.b07z.sepia.server.core.tools.Debugger;
import net.b07z.sepia.server.core.tools.Is;
import net.b07z.sepia.server.core.tools.JSON;
import net.b07z.sepia.server.core.users.AuthenticationAssistAPI;

/**
 * Some config settings that are shared across multiple SEPIA components.
 * 
 * @author Florian Quirin
 *
 */
public class ConfigDefaults {
	
	//note: see also: data/Defaults
	
	//Logger - moved to properties file
	/*
	static{
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "info");
		System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_DATE_TIME_KEY, "true");
		System.setProperty(org.slf4j.impl.SimpleLogger.DATE_TIME_FORMAT_KEY, "yyyy-MM-dd HH:mm:ss");
	}
	*/

	//Clients / Authentication
	public static String defaultClientInfo = "web_app_v1.0.0";		//in case the client does not submit this info to the server use this.
	
	//Modules
	public static String defaultAuthModule = AuthenticationAssistAPI.class.getCanonicalName();
	
	//APIs
	public static String defaultAssistAPI = "";
	public static String defaultTeachAPI = "";
	//TODO: add socket-API?
	
	//Cluster
	public static String clusterKey = "";
	
	//Users
	public static String defaultAssistantUserId = ""; 		//this needs to be identical to e.g. Config.assistantId if the server uses it
	
	//Privacy policy links
	public static String privacyPolicyLink = ""; 			//REPLACE THIS in config file for production!
	
	/**
	 * Set some configuration defaults, e.g.:<br>
	 * "defaultAssistAPI"
	 */
	public static void setupCoreTools(JSONObject config){
		if (config.containsKey("defaultAssistAPI"))		
			defaultAssistAPI = JSON.getString(config, "defaultAssistAPI");
		if (config.containsKey("defaultTeachAPI"))		
			defaultTeachAPI = JSON.getString(config, "defaultTeachAPI");
		if (config.containsKey("defaultAuthModule"))		
			defaultAuthModule = JSON.getString(config, "defaultAuthModule");
		if (config.containsKey("clusterKey"))		
			clusterKey = JSON.getString(config, "clusterKey");
		if (config.containsKey("defaultAssistantUserId"))		
			defaultAssistantUserId = JSON.getString(config, "defaultAssistantUserId");
		if (config.containsKey("privacyPolicy"))
			privacyPolicyLink = JSON.getString(config, "privacyPolicy");
				
		//Microservices API-Keys
		if (config.containsKey("DeutscheBahnOpenApiKey"))		
			DbStationResults.setApiKey(JSON.getString(config, "DeutscheBahnOpenApiKey"));
	}
	
	/**
	 * Check if core-tools have been initialized.
	 * @return
	 */
	public static boolean areCoreToolsSet(){
		boolean apisSet = (Is.notNullOrEmpty(defaultAssistAPI) && Is.notNullOrEmpty(defaultTeachAPI));
		boolean clusterSet = Is.notNullOrEmpty(clusterKey);
		boolean modulesSet = Is.notNullOrEmpty(defaultAuthModule);
		boolean clientSet = Is.notNullOrEmpty(defaultClientInfo);
		boolean usersSet = Is.notNullOrEmpty(defaultAssistantUserId);
		boolean policiesSet = Is.notNullOrEmpty(privacyPolicyLink);
		//Microservices are optional
		
		return (apisSet && clusterSet && modulesSet && clientSet && usersSet && policiesSet);
	}
	
	/**
	 * Call assistAPI to get relevant cluster-data. Uses clusterKey to identify. 
	 * @return JSONObject with e.g. "assistantUserId" and more (or null for connection error)
	 */
	public static JSONObject getAssistantClusterData(){
		JSONObject res = Connectors.httpFormPOST(defaultAssistAPI + "cluster", 
				ContentBuilder.postForm("sKey", clusterKey));
		if (!Connectors.httpSuccess(res)){
			Debugger.println("Could not reach AssistAPI at " + defaultAssistAPI, 1);
			return null;
		}
		String resultState = JSON.getString(res, "result");
		if (resultState == null || !resultState.equals("success")){
			Debugger.println("AssistAPI answered with error: " + JSON.getString(res, "error"), 1);
			return null;
		}
		return res;
	}
}
