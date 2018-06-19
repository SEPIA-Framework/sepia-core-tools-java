package net.b07z.sepia.server.core.microservices;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.tools.Connectors;
import net.b07z.sepia.server.core.tools.Converters;
import net.b07z.sepia.server.core.tools.DateTime;
import net.b07z.sepia.server.core.tools.URLBuilder;

public class DbStationResults{
	
	private static final String FAHRPLAN_API_BASE_URL = "https://open-api.bahn.de/bin/rest.exe";
    private static String DB_API_KEY = "";
    
	JSONArray stations;
	List<DbStation> stationsList;

	public DbStationResults(String searchName, String languageCode){
		String url = URLBuilder.getString(FAHRPLAN_API_BASE_URL,
                "/location.name?authKey=", DB_API_KEY,
                "&lang=", languageCode,
                "&format=json&input=", searchName);
        JSONObject jsonResult = Connectors.httpGET(url);
        if (Connectors.httpSuccess(jsonResult)){
	        try{
	        	JSONArray stations = (JSONArray) ((JSONObject) jsonResult.get("LocationList")).get("StopLocation");
	        	this.stations = stations;
	        
	        }catch (Exception e){
	        	System.err.println(DateTime.getLogDate() + " ERROR - DbStationResults.java - API call failed, base-URL: " + FAHRPLAN_API_BASE_URL);
	        	e.printStackTrace();
	        	this.stations = null;
	        }
        }
	}
	public DbStationResults(JSONArray stations){
		this.stations = stations;
	}
	
	public static void setApiKey(String newKey){
		DB_API_KEY = newKey;
	}
	
	public JSONArray getJSONArray(){
		return stations;
	}
	public List<DbStation> getList(){
		if (stationsList != null){
			return stationsList;
		}else if (stations == null){
			return null;
		}
		try{
			stationsList = new ArrayList<>();
			for (int i=0; i<stations.size(); i++){
				stationsList.add(new DbStation((JSONObject) stations.get(i)));
			}
			return stationsList;
			
		}catch (Exception e){
			stationsList = null;
			e.printStackTrace();
			return null;
		}
	}
	
	public DbStation getFirst(){
		if (stations != null && !stations.isEmpty()){
			return new DbStation((JSONObject) stations.get(0));
		}else{
			return null;
		}
	}

	public DbStation getClosest(String lat, String lng){
		return getClosest(Double.parseDouble(lat), Double.parseDouble(lng));
	}
	public DbStation getClosest(double lat, double lng){
		if (stations != null && !stations.isEmpty()){
			try{
				double bestDistance = Double.MAX_VALUE;
				DbStation bestStation = null;
				for (int i=0; i<stations.size(); i++){
					DbStation thisStation = new DbStation((JSONObject) stations.get(i));
					double thisDistance = thisStation.getApproxDistance(lat, lng);
					if (thisDistance < bestDistance){
						bestDistance = thisDistance;
						bestStation = thisStation;
					}
				}
				return bestStation;
				
			}catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
	
	//TODO: get best by name
	//DbStationResult getBestMatch(String name){}
	
	//------------ single station -------------
	
	public class DbStation{
		String stationId;
		String stationName;
		double longitude;
		double latitude;
		
		JSONObject data;
		
		public DbStation(JSONObject data){
			this.data = data;
			
			this.stationName = (String) data.get("name");
			this.stationId = (String) data.get("id");
			this.longitude = Converters.obj2Double(data.get("lon"));
			this.latitude = Converters.obj2Double(data.get("lat"));
		}
		
		@Override
		public String toString(){
			return stationName;
		}
		
		public String getName(){
			return stationName;
		}
		public String getId(){
			return stationId;
		}
		public double getLatitude(){
			return latitude;
		}
		public double getLongitude(){
			return longitude;
		}
		public double getApproxDistance(double lat, double lng){
			try{
				return Math.sqrt((Math.pow(lat - latitude, 2) + Math.pow(lng - longitude, 2)));
			
			}catch (Exception e){
				e.printStackTrace();
				return Double.MAX_VALUE;
			}
		}
	}
}
