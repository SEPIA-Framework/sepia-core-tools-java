package tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import net.b07z.sepia.server.core.tools.Connectors;
import net.b07z.sepia.server.core.tools.Connectors.HttpClientResult;
import net.b07z.sepia.server.core.tools.CsvUtils;

public class Test_CsvUtils {

	public static void main(String[] args) throws Exception{
		String country = "DE"; //null;
		getCovid19Data(country);
	}
	
	private static List<List<String>> getCovid19Data(String country) throws RuntimeException, Exception{
		List<List<String>> data = CsvUtils.readStreamAsRows(
				getLinesFromUrl("https://opendata.ecdc.europa.eu/covid19/casedistribution/csv/"), 
				CsvUtils.DEFAULT_SEPARATOR, CsvUtils.DEFAULT_QUOTE, row -> {
					//return true;
					if (country == null || row.contains(country)){
						return true;
					}else{
						return false;
					}
				}
		);
		int casesSum = 0;
		int deathsSum = 0;
		if (country == null){
			//List<String> header = data.remove(0);
			data.remove(0);
			for (List<String> row : data){
				casesSum += Integer.parseInt(row.get(4));
				deathsSum += Integer.parseInt(row.get(5));
			}
			
		}else{
			for (List<String> row : data){
				casesSum += Integer.parseInt(row.get(4));
				deathsSum += Integer.parseInt(row.get(5));
			}
		}
		System.out.println("Country: " + (country == null? "WORLD" : country));
		System.out.println("Cases: " + casesSum);
		System.out.println("Deaths: " + deathsSum);
		if (country != null){
			System.out.println("Last 3 days: ");
			int N = data.size();
			//for (int i=N-1; i>N-4; i--){
			for (int i=0; (i<3 && i<N); i++){
				List<String> row = data.get(i);
				System.out.println(row.get(0) + " - new cases: " + row.get(4) + " - new deaths: " + row.get(5));
			}
		}
		return data;
	}
	
	private static Stream<String> getLinesFromUrl(String url) throws Exception{
		Map<String, String> headers = new HashMap<>();
		HttpClientResult res = Connectors.apacheHttpGET(url, headers);
		//System.out.println(res.content);
		//[dateRep, day, month, year, cases, deaths, countriesAndTerritories, geoId, countryterritoryCode, popData2018]
		return Arrays.stream(res.content.split("(\\r\\n|\\n)"));
	}

}
