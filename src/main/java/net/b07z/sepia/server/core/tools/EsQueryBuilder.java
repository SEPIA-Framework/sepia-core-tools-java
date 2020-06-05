package net.b07z.sepia.server.core.tools;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Class to help build Elasticsearch queries.
 * 
 * @author Florian Quirin
 *
 */
public class EsQueryBuilder {
	
	//the "match all" query
	public static final JSONObject matchAll = JSON.make(
			"query", JSON.make("match_all", new JSONObject())
	);
	
	public static class QueryElement{
		String field;
		Object value;
		String analyzer;
		
		/**
		 * Examples:<br>
		 * new QueryElement("channelId", channelId, "keylower")<br>
		 * NOTE: keylower is a custom analyzer
		 */
		public QueryElement(String field, Object value, String analyzer){
			this.field = field;
			this.value = value;
			if (analyzer != null && !analyzer.isEmpty()){
				this.analyzer = analyzer;
			}
		}
		/**
		 * Examples:<br>
		 * new QueryElement("channelId", channelId)<br>
		 * new QueryElement("timeUNIX", JSON.make("lt", olderThanUnix))<br>
		 */
		public QueryElement(String field, Object value){
			this.field = field;
			this.value = value;
		}
		
		public JSONObject getJSON(){
			if (analyzer == null) return JSON.make(field, value);
			else return JSON.make(field, JSON.make("analyzer", analyzer, "query", value));
		}
		public JSONObject getAsMatch(){
			return JSON.make("match", getJSON());
		}
		public JSONObject getAsRange(){
			return JSON.make("range", getJSON());
		}
	}
	
	/**
	 * Build a query that must match a set of query elements 
	 */
	public static JSONObject getBoolMustMatch(List<QueryElement> matches){
		JSONArray must = new JSONArray();
		for (QueryElement qe : matches){
			JSON.add(must, qe.getAsMatch());
		}
		return JSON.make("query", JSON.make("bool", JSON.make("must", must)));
	}
	/* before I knew that an array is accepted as well:
	 * 	public static String getBoolMustMatch(List<QueryElement> matches){
		String query = "" +
				"{\"query\": {" +
					"\"bool\": {";
					for (QueryElement qe : matches){
						query += "\"must\": {" +
							JSON.make("match", qe.getJSON()).toJSONString().replaceAll("^\\{|\\}$", "") +
						"},";
					}
					query = query.replaceFirst(",$", "") +  
					"}" +
				"}}";
		return query;	
	} */
	
	/**
	 * Build a query that should match at least one of the query elements 
	 */
	public static JSONObject getBoolShouldMatch(List<QueryElement> shouldMatches){
		JSONArray should = new JSONArray();
		for (QueryElement qe : shouldMatches){
			JSON.add(should, qe.getAsMatch());
		}
		return JSON.make("query", JSON.make("bool", JSON.make("should", should, "minimum_should_match", 1)));
	}
	
	/**
	 * Build a query that must match some elements and should (AT LEAST ONE) match another set of query elements  
	 */
	public static JSONObject getBoolMustAndShoudMatch(List<QueryElement> mustMatches, List<QueryElement> shouldMatches){
		//TODO: untested
		JSONArray must = new JSONArray();
		JSONArray should = new JSONArray();
		for (QueryElement qe : mustMatches){
			JSON.add(must, qe.getAsMatch());
		}
		for (QueryElement qe : shouldMatches){
			JSON.add(should, qe.getAsMatch());
		}
		return JSON.make("query", JSON.make("bool", JSON.make("must", must, "should", should, "minimum_should_match", 1)));
	}
	
	/**
	 * Build a query that must match some elements and must NOT match another set of query elements  
	 */
	public static JSONObject getBoolMustAndMustNotMatch(List<QueryElement> mustMatches, List<QueryElement> mustNotMatches){
		JSONArray must = new JSONArray();
		JSONArray mustNot = new JSONArray();
		for (QueryElement qe : mustMatches){
			JSON.add(must, qe.getAsMatch());
		}
		for (QueryElement qe : mustNotMatches){
			JSON.add(mustNot, qe.getAsMatch());
		}
		return JSON.make("query", JSON.make("bool", JSON.make("must", must, "must_not", mustNot)));
	}
	
	/**
	 * Build a query that must match certain elements and value ranges, e.g. must match a certain message-ID with timestamp older than xyz.    
	 */
	public static JSONObject getBoolMustAndRangeMatch(List<QueryElement> mustMatches, List<QueryElement> rangeMatches){
		JSONArray must = new JSONArray();
		JSONArray filter = new JSONArray();
		for (QueryElement qe : mustMatches){
			JSON.add(must, qe.getAsMatch());
		}
		for (QueryElement qe : rangeMatches){
			JSON.add(filter, qe.getAsRange());
		}
		return JSON.make("query", JSON.make("bool", JSON.make("must", must, "filter", filter)));
	}

	/**
	 * Build a query for a nested entry that must match a set of query elements. Note: each query field has to include the path as well like this:
	 * path=sentences, query element 1: sentences.user=..., ... 
	 * @param path - path to the nested object
	 * @param matches - QueryElements to match
	 */
	public static JSONObject getNestedBoolMustMatch(String path, List<QueryElement> matches){
		JSONObject mustQuery = getBoolMustMatch(matches);
		return JSON.make("query", JSON.make("nested", JSON.make("path", path, "query", mustQuery.get("query"))));
	}
	
	/**
	 * Make a query that is a mix of several root matches and several nested matches (that are all in the same nest-path).
	 * Note: Multiple nested paths are not yet supported.  
	 * @param rootMatches - matches on root level
	 * @param nestPath - path to the nested object
	 * @param nestMatches - matches at the nest-path
	 */
	public static JSONObject getMixedRootAndNestedBoolMustMatch(List<QueryElement> rootMatches, String nestPath, List<QueryElement> nestMatches){
		//root
		JSONArray mustArray = new JSONArray();
		for (QueryElement qe : rootMatches){
			JSON.add(mustArray, qe.getAsMatch());
		}
		//nested
		JSONObject nestedMustMatches = getBoolMustMatch(nestMatches);
		JSON.add(mustArray, JSON.make("nested", JSON.make("path", nestPath, "query", nestedMustMatches.get("query"))));
		//merge
		return JSON.make("query", JSON.make("bool", JSON.make("must", mustArray)));
	}
	
	/**
	 * Build a range query with a custom combination of greater-than (gt), greater-or-equal (gte),
	 * less-than (lt) and less-or-equal (lte). Conditions that are not needed should be set null.<br>
	 * 
	 * For details see: https://www.elastic.co/guide/en/elasticsearch/reference/5.3/query-dsl-range-query.html
	 * 
	 * @param matchField - field to apply range query to
	 * @param gt - greater-than, number or string (or null)
	 * @param gte - greater-or-equal, number or string (or null)
	 * @param lt - less-than, number or string (or null)
	 * @param lte - less-or-equal, number or string (or null)
	 * @return
	 */
	public static JSONObject buildRangeQuery(String matchField, Object gt, Object gte, Object lt, Object lte){
		JSONObject conditions = new JSONObject();
		if (gt != null) JSON.put(conditions, "gt", gt);
		if (gte != null) JSON.put(conditions, "gte", gte);
		if (lt != null) JSON.put(conditions, "lt", lt);
		if (lte != null) JSON.put(conditions, "lte", lte);
		return JSON.make("query", JSON.make("range", JSON.make(matchField, conditions)));
	}
}
