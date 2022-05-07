package net.b07z.sepia.server.core.tools;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Read property files with some special features like default mapping.
 */
public class PropertiesReader {

	private String filePath;
	private Properties properties;
	private Map<String, String> propertiesReadMap;		//store read properties including defaults
	
	/**
	 * Create a new properties reader.
	 */
	public PropertiesReader(String filePath){
		this.filePath = filePath;
		this.propertiesReadMap = new LinkedHashMap<>();		//NOTE: keeps insert order
	}
	
	/**
	 * Return underlying properties.
	 */
	public Properties getProperties(){
		return this.properties;
	}
	
	/**
	 * Get a map of all values read including defaults.
	 */
	public Map<String, String> getReadValuesMap(){
		return this.propertiesReadMap;
	}
	
	/**
	 * Load file.
	 */
	public void loadFile() throws Exception {
		this.properties = FilesAndStreams.loadSettings(this.filePath);
	}
	
	/**
	 * Get property.
	 */
	public String getStringProperty(String key){
		String val = this.properties.getProperty(key);
		propertiesReadMap.put(key, val);
		return val;
	}
	/**
	 * Get property as string or default.
	 */
	public String getStringPropertyOrDefault(String key, String defaultVal){
		String val = this.properties.getProperty(key, defaultVal);
		propertiesReadMap.put(key, val);
		return val;
	}
	/**
	 * Get property as boolean or default.
	 */
	public boolean getBooleanOrDefault(String key, boolean defaultVal){
		return Boolean.valueOf(this.getStringPropertyOrDefault(key, Boolean.toString(defaultVal)));
	}
	/**
	 * Get property as integer or default.
	 */
	public int getIntegerOrDefault(String key, int defaultVal){
		return Integer.valueOf(this.getStringPropertyOrDefault(key, Integer.toString(defaultVal)));
	}
	/**
	 * Get property as long or default.
	 */
	public long getLongOrDefault(String key, long defaultVal){
		return Long.valueOf(this.getStringPropertyOrDefault(key, Long.toString(defaultVal)));
	}
	/**
	 * Get property as float or default.
	 */
	public float getFloatOrDefault(String key, float defaultVal){
		return Float.valueOf(this.getStringPropertyOrDefault(key, Float.toString(defaultVal)));
	}
	/**
	 * Get property as double or default.
	 */
	public double getDoubleOrDefault(String key, double defaultVal){
		return Double.valueOf(this.getStringPropertyOrDefault(key, Double.toString(defaultVal)));
	}
}
