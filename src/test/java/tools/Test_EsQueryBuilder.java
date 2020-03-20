package tools;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import net.b07z.sepia.server.core.tools.EsQueryBuilder;
import net.b07z.sepia.server.core.tools.JSON;
import net.b07z.sepia.server.core.tools.EsQueryBuilder.QueryElement;

public class Test_EsQueryBuilder {

	public static void main(String[] args){
		/* -- Elasticsearch queries -- */
		
		//double-match for simple field
		String query = getBoolMustMatch();
		System.out.println("query must-must: " + query);
		
		//double-match with must and should for simple field
		query = getBoolMustAndShoudMatch();
		System.out.println("query should-must: " + query);
		
		//double-match for nested field
		query = getNestedBoolMustMatch();
		System.out.println("nested query must-must: " + query);
		
		//double-match for nested field
		query = getMixedRootAndNestedBoolMustMatch();
		System.out.println("mixed root and nested query must-must: " + query);
		
		//getAnswersByType query with JsonGenerator
		query = getAnswersQuery();
		System.out.println("getAnswersByType query with JsonGenerator: " + query);
		//getAnswersByType query with JsonGenerator
		query = getAnswersQueryWithBuilder();
		System.out.println("getAnswersByType query with builder: " + query);
		
		//match and range
		query = getBoolMustAndRangeMatch();
		System.out.println("getBoolMustAndRangeMatch query with builder: " + query);
		System.out.println("getBoolMustAndRangeMatch query with direct build: " + EsQueryBuilder.buildRangeQuery("timeUNIX", "30", null, "100", null));
	}
	
/* -- Elasticsearch queries -- */
	
	private static String getBoolMustMatch(){
		List<QueryElement> matches = new ArrayList<>(); 
		matches.add(new QueryElement("user", "theUserId01"));
		matches.add(new QueryElement("title", "myTitleToMatch"));
		String query = EsQueryBuilder.getBoolMustMatch(matches).toJSONString();
		return query;
	}
	
	private static String getBoolMustAndShoudMatch(){
		//must
		List<QueryElement> mustMatches = new ArrayList<>(); 
		mustMatches.add(new QueryElement("user", "theUserId01"));
		mustMatches.add(new QueryElement("title", "myTitleToMatch"));
		//should
		List<QueryElement> shouldMatches = new ArrayList<>(); 
		shouldMatches.add(new QueryElement("name", "aName"));
		shouldMatches.add(new QueryElement("name", "bName"));
		
		String query = EsQueryBuilder.getBoolMustAndShoudMatch(mustMatches, shouldMatches).toJSONString();
		return query;
	}
	
	private static String getBoolMustAndRangeMatch(){
		//must
		List<QueryElement> mustMatches = new ArrayList<>(); 
		mustMatches.add(new QueryElement("user", "theUserId01"));
		mustMatches.add(new QueryElement("title", "myTitleToMatch"));
		//should
		List<QueryElement> rangeMatches = new ArrayList<>(); 
		rangeMatches.add(new QueryElement("timeUNIX", JSON.make("lt", 60000l, "gt", 30000l)));
		rangeMatches.add(new QueryElement("entries", JSON.make("gte", 25)));
		
		String query = EsQueryBuilder.getBoolMustAndRangeMatch(mustMatches, rangeMatches).toJSONString();
		return query;
	}
	
	private static String getNestedBoolMustMatch(){
		List<QueryElement> matches = new ArrayList<>(); 
		matches.add(new QueryElement("sentences.source", "SDK"));
		matches.add(new QueryElement("sentences.source", "uid1011.demo"));
		matches.add(new QueryElement("sentences.user", "uid1011"));
		String query = EsQueryBuilder.getNestedBoolMustMatch("sentences", matches).toJSONString();
		return query;
	}
	
	private static String getMixedRootAndNestedBoolMustMatch(){
		List<QueryElement> rootMatches = new ArrayList<>(); 
		rootMatches.add(new QueryElement("_id", "AVumVgd3n6LZD21-2I7w"));
		
		String nestPath = "sentences";
		List<QueryElement> nestedMatches = new ArrayList<>(); 
		nestedMatches.add(new QueryElement(nestPath + ".user", "uid1011"));
		
		JSONObject queryJson = EsQueryBuilder.getMixedRootAndNestedBoolMustMatch(rootMatches, nestPath, nestedMatches);
		return queryJson.toJSONString();
	}
	
	private static String getAnswersQuery(){
		String[] users = {"uid001", "uid002"};
		String answerType = "chat_01a";
		String languageOrNull = "de";
		
		StringWriter sw = new StringWriter();
		try {
			try (JsonGenerator g = new JsonFactory().createGenerator(sw)) {
				g.writeStartObject();
					g.writeNumberField("size", 10000);  // let's read the maximum possible
					g.writeObjectFieldStart("query");
						g.writeObjectFieldStart("bool");
							//must
							g.writeArrayFieldStart("must");
								//type
								makeJsonQueryTerm(g, "type", answerType.toLowerCase());
								//language
								if (languageOrNull != null) {
									makeJsonQueryTerm(g, "language", languageOrNull.toLowerCase());
								}
							g.writeEndArray();
							//should
							g.writeArrayFieldStart("should");
								//user
								for (String u : users){
									makeJsonQueryTerm(g, "user", u.trim().toLowerCase());
								}
							g.writeEndArray();
							g.writeNumberField("minimum_should_match", 1);  //at least one of the shoulds must match
						g.writeEndObject();
					g.writeEndObject();
				g.writeEndObject();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sw.toString();
	}
	private static void makeJsonQueryTerm(JsonGenerator g, String key, String value) throws IOException {
		g.writeStartObject();
			g.writeObjectFieldStart("term");
				g.writeStringField(key, value.toLowerCase());
			g.writeEndObject();
		g.writeEndObject();
	}
	private static String getAnswersQueryWithBuilder(){
		String[] users = {"uid001", "uid002"};
		String answerType = "chat_01a";
		String languageOrNull = "de";
		
		//must match
		List<QueryElement> mustMatches = new ArrayList<>(); 
		mustMatches.add(new QueryElement("type", answerType.toLowerCase()));
		if (languageOrNull != null) {
			mustMatches.add(new QueryElement("language", languageOrNull.toLowerCase()));
		}
		//should match (at least one)
		List<QueryElement> shouldMatches = new ArrayList<>(); 
		for (String u : users){
			shouldMatches.add(new QueryElement("user", u.trim().toLowerCase()));
		}
		//add size
		JSONObject queryJson = EsQueryBuilder.getBoolMustAndShoudMatch(mustMatches, shouldMatches);
		JSON.put(queryJson, "size", 10000);  // let's read the maximum possible
		return queryJson.toJSONString();
	}

}
