package microservices;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import net.b07z.sepia.server.core.data.Language;
import net.b07z.sepia.server.core.microservices.DbStationResults;
import net.b07z.sepia.server.core.microservices.DbStationResults.DbStation;
import net.b07z.sepia.server.core.tools.FilesAndStreams;

public class TestMicroservices {
	
	private static String testConfig = "TestData/microservices.properties";		//Data for testing (e.g. contains API keys)
	
	@Test
	public void testDbStationResults() throws IOException {
		Properties settings;
		String dbApiKey = null;
		try {
			settings = FilesAndStreams.loadSettings(testConfig);
			dbApiKey = settings.getProperty("deutsche_bahn_open_api_key");
			
		} catch (Exception e) {
			System.out.println("NOTE: JUnit-microservices: skipped dBStationResults testing due to missing test-data!");
		}
		
		if (dbApiKey != null){
			DbStationResults.setApiKey(dbApiKey);
			
			String langCode = Language.DE.toValue();
			String searchName = "Berlin City";
			String longitude = "13.4346";
			String latitude = "52.5111";
			
			DbStationResults dbStations = new DbStationResults(searchName, langCode);
			DbStation dbStationFirst = dbStations.getFirst();
			//System.out.println("DbStation first: " + dbStationFirst.toString()); 			//debug
			DbStation dbStationClosest = dbStations.getClosest(latitude, longitude);
			//System.out.println("DbStation closest: " + dbStationClosest.toString()); 		//debug
			
			assertThat(dbStationFirst.getName(), is("Berlin Hbf"));
			assertThat(dbStationFirst.getId(), is("008011160"));
			assertThat(dbStationClosest.toString(), is("Berlin Ostbahnhof"));
			assertThat(dbStationClosest.getId(), is("008010255"));
		}
	}

}
