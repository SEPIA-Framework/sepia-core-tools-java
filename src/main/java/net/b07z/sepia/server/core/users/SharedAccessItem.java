package net.b07z.sepia.server.core.users;

import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.tools.JSON;

/**
 * Shared access permission item (one entry of the permissions list).
 * 
 * @author Florian Quirin
 *
 */
public class SharedAccessItem {
	
	private String user;			//user to share with
	private String device;			//device of target (sharing client)
	private JSONObject details;		//specific data for parent type
	
	public SharedAccessItem(){}
	
	/**
	 * Create item.
	 * @param user - user ID to share with (who wants access?)
	 * @param device - target device ID (where to execute?)
	 * @param details - specific JSON data depending on parent data-type (granular permissions, e.g. action 'type' of 'remoteAction' etc.)
	 */
	public SharedAccessItem(String user, String device, JSONObject details){
		this.user = user;
		this.device = device;
		this.details = details;
	}

	public String getUser(){
		return user;
	}
	public void setUser(String user){
		this.user = user;
	}
	
	public String getDevice(){
		return device;
	}
	public void setDevice(String device){
		this.device = device;
	}
	
	public JSONObject getDetails(){
		return details;
	}
	public void setDetails(JSONObject details){
		this.details = details;
	}
	
	public JSONObject toJson(){
		return JSON.make("user", user, "device", device, "details", details);
	}
	public static SharedAccessItem fromJson(JSONObject json){
		return new SharedAccessItem(
			JSON.getStringOrDefault(json, "user", null),
			JSON.getStringOrDefault(json, "device", null),
			JSON.getJObject(json, "details")
		);
	}
	
	@Override
	public String toString(){
		return toJson().toString();
	}
}
