package net.b07z.sepia.server.core.database;

import org.json.simple.JSONObject;

/**
 * Interface for classes that supply database access. First and foremost this is meant to be an interface for Elasticsearch (and not DynamoDB, but feel free ...).
 * 
 * @author Florian Quirin
 *
 */
public interface DatabaseInterface {
	
	/**
	 * Set the data/properties/values of an item of "type" at "index". 
	 * @param index - index or table name like e.g. "account" or "knowledge"
	 * @param type - subclass name, e.g. "user", "lists", "banking" (for account) or "geodata" and "dictionary" (for knowledge) 
	 * @param item_id - unique item/id name, e.g. user email address, dictionary word or geodata location name
	 * @param data - JSON string with data objects that should be stored for index/type/item, e.g. {"name":"john"}
	 * @return error code indicating success (0) or different errors (1 - database not reached) etc. ...
	 */
	public int setItemData(String index, String type, String item_id, JSONObject data);
	
	/**
	 * Set the data/properties/values of an arbitrary item of "type" at "index". Use this if you don't care about the unique id like
	 * when you store blog posts. The item id will be generated automatically.
	 * @param index - index or table name like e.g. "homepage"
	 * @param type - subclass name, e.g. "blogpost"
	 * @param data - JSON string with data objects that should be stored for index/type/any_id, e.g. {"title":"Hot News", "body":"bla bla...", "author":"john james"}
	 * @return JSON with "_id" of created doc and error "code" indicating success (0) or different errors (1 - database not reached) etc. ...
	 */
	public JSONObject setAnyItemData(String index, String type, JSONObject data);
	
	/**
	 * Get item at path "index/type/item_id"
	 * @param index - index or table name like e.g. "account" or "knowledge"
	 * @param type - subclass name, e.g. "user", "lists", "banking" (for account) or "geodata" and "dictionary" (for knowledge) 
	 * @param item_id - unique item/id name, e.g. user email address, dictionary word or geodata location name
	 * @return JSONObject with result or error description
	 */
	public JSONObject getItem(String index, String type, String item_id);
	
	/**
	 * Get filtered entries of the item at path "index/type/item_id". Use this if you want for example only a user name or something.
	 * @param index - index or table name like e.g. "account" or "knowledge"
	 * @param type - subclass name, e.g. "user", "lists", "banking" (for account) or "geodata" and "dictionary" (for knowledge) 
	 * @param item_id - unique item/id name, e.g. user email address, dictionary word or geodata location name
	 * @param filters - String array with filters like ["name", "address", "language", "age"]
	 * @return JSONObject with result or error description
	 */
	public JSONObject getItemFiltered(String index, String type, String item_id, String[] filters);
	
	/**
	 * Update or create the data/properties/values of an item of "type" at "index". 
	 * @param index - index or table name like e.g. "account" or "knowledge"
	 * @param type - subclass name, e.g. "user", "lists", "banking" (for account) or "geodata" and "dictionary" (for knowledge) 
	 * @param item_id - unique item/id name, e.g. user email address, dictionary word or geodata location name
	 * @param data - JSON string with data objects that should be stored for index/type/item, e.g. {"name":"john"}
	 * @return error code indicating success (0) or different errors (1 - database not reached) etc. ...
	 */
	public int updateItemData(String index, String type, String item_id, JSONObject data);
	
	/**
	 * Search at "path" for a keyword.
	 * @param path - can be index, type or item, e.g. "index/type/item_id" or only "index/"
	 * @param search_term - something to search like "name:John" or simply "John" or "*" for all.
	 * @return JSONObject with search result or error description
	 */
	public JSONObject searchSimple(String path, String search_term);
	
	/**
	 * Search at "path" for something via a query in JSON format
	 * @param path - can be index, type or item, e.g. "index/type/item_id" or only "index/"
	 * @param jsonQuery - query as JSON string
	 * @return JSONObject with search result or error description
	 */
	public JSONObject searchByJson(String path, String jsonQuery);
	
	/**
	 * Delete item of "type" at "index".
	 * @param index - index or table name like e.g. "account"
	 * @param type - subclass name, e.g. "user"
	 * @param item_id - item to delete, e.g. a user_id (email)
	 * @return error code indicating success (0) or different errors (1 - database not reached) etc. ...
	 */
	public int deleteItem(String index, String type, String item_id);
	
	/**
	 * Delete any object like "index" (with all entries), "type" or "item"
	 * @param path - path of what you want to delete like "account/" or "account/banking/"
	 * @return error code indicating success (0) or different errors (1 - database not reached) etc. ...
	 */
	public int deleteAnything(String path);
	
	/**
	 * Search at "path" for something via a query in JSON format and if it exists delete it.
	 * @param path - can be index, type or item, e.g. "index/type/item_id" or only "index/"
	 * @param jsonQuery - query as JSON string
	 * @return JSONObject with delete result or error description
	 */
	public JSONObject deleteByJson(String path, String jsonQuery);

}
