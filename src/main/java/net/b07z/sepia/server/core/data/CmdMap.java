package net.b07z.sepia.server.core.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.tools.JSON;

/**
 * This class holds the info that connects a command to one or more services and their permissions.
 * 
 * @author Florian Quirin
 *
 */
public class CmdMap {
	
	public static String MAP_TYPE = "services";
	public static String CUSTOM = "custom";
	public static String SYSTEM = "system";

	private String command;
	private List<String> services;
	private List<String> permissions;
	
	@SuppressWarnings("unchecked")
	/**
	 * Import command map from JSON.
	 * @param entry
	 */
	public CmdMap(JSONObject entry){
		this.command = JSON.getString(entry, "command");
		this.services = new ArrayList<>((JSONArray)entry.get("services"));
		this.permissions = new ArrayList<>((JSONArray)entry.get("permissions"));
	}
	/**
	 * Create a new command map.
	 * @param command
	 * @param services
	 * @param permissions
	 */
	public CmdMap(String command, List<String> services, List<String> permissions){
		this.command = command;
		this.services = services;
		this.permissions = permissions;
	}
	
	@Override
    public int hashCode() {
        //TODO: this is to force the equals method but should we maybe use a real hashCode?
        return 1;
    }
	@Override
	public boolean equals(Object map){
		//these maps should replace each other when the command is equal
		if (!(map instanceof CmdMap))return false;
		CmdMap otherMap = (CmdMap) map;
		if (otherMap.getCommand().equals(command)){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public String toString(){
		return (this.command + " - services: " + this.services.size());
	}
	
	public String getCommand(){
		return command;
	}
	
	public List<String> getServices(){
		return services;
	}
	
	public boolean hasPermission(String permission){
		return (permissions.contains(permission) || permissions.contains("all"));
	}
	
	public JSONObject getJSON(){
		JSONObject jo = new JSONObject();
		JSON.add(jo, "command", command);
		JSON.add(jo, "services", services);
		JSON.add(jo, "permissions", permissions);
		return jo;
	}
	
	/**
	 * Take a JSONArray with mappings and make a List&lt;CmdMap&gt;.
	 * Returns empty list if no mappings are found (array is null or empty).
	 */
	public static List<CmdMap> makeMapList(JSONArray cmdMaps){
		List<CmdMap> mapList = new ArrayList<>();
		if (cmdMaps != null){
			for (Object o : cmdMaps){
				mapList.add(new CmdMap((JSONObject) o));
			}
		}
		return mapList;
	}
}
