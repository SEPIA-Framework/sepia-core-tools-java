package tools;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.json.simple.JSONObject;
import org.junit.Test;

import net.b07z.sepia.server.core.tools.ContentBuilder;
import net.b07z.sepia.server.core.tools.Converters;
import net.b07z.sepia.server.core.tools.JSON;
import net.b07z.sepia.server.core.tools.Security;

public class ToolsTest {

	@Test
	public void testConverters() {
		String test = "<first>Mister<nick><last>Tester<nick>";
		
		JSONObject jo = Converters.dataString_2_JSON(test);
		
		assertThat(jo.get("first"), is("Mister"));
		assertThat(jo.get("last"), is("Tester"));
		assertThat(jo.get("nick"), is(""));
		assertNull(jo.get("missingData"));
		//System.out.println("Converters.dataString_2_JSON - F: " + jo.get("first") + ", L: " + jo.get("last") + ", N: " + jo.get("nick"));
	}
	
	@Test
	public void testContentBuilder() {
		String formContent = ContentBuilder.postForm("a", 1, "b", 2);
		assertTrue(formContent.equals("a=1&b=2"));
		formContent = ContentBuilder.postForm("a", "one", "b", 2, "c", "last");
		assertTrue(formContent.equals("a=one&b=2&c=last"));
	}
	
	@Test
	public void testPasswordEncryption() throws Exception {
		//long tic = System.currentTimeMillis();
		byte[] salt0 = Security.getRandomSalt(32);
		String saltHex = Security.bytearrayToHexString(salt0);
		byte[] salt0b = Security.hexToByteArray(saltHex);
		byte[] salt1 = "salt".getBytes("UTF-8");
		byte[] salt2 = "saltSALTsaltSALTsaltSALTsaltSALTsalt".getBytes("UTF-8");
		byte[] salt3 = "sa\0lt".getBytes("UTF-8");
		//String hash0 = Security.bytearrayToHexString(Security.getEncryptedPassword("§_öÜ47@'*Ä:892", salt0, 20000, 32));
		//long toc = System.currentTimeMillis() - tic;
		String hash1 = Security.bytearrayToHexString(Security.getEncryptedPassword("password", salt1, 4096, 32));
		String hash2 = Security.bytearrayToHexString(Security.getEncryptedPassword("passwordPASSWORDpassword", salt2, 4096, 40));
		String hash3 = Security.bytearrayToHexString(Security.getEncryptedPassword("pass\0word", salt3, 4096, 16));
		
		//System.out.println("test salt: " + Security.bytearrayToHexString(salt0));
		//System.out.println("test hash: " + hash0);
		//System.out.println("test time (ms): " + toc);
		
		assertTrue(Arrays.equals(salt0b, salt0));
		assertThat(hash1, is("c5e478d59288c841aa530db6845c4c8d962893a001ce4e11a4963873aa98134a"));
		assertThat(hash2, is("348c89dbcbd32b2f32d814b8116e84cf2b17347ebc1800181c4e2a1fb8dd53e1c635518c7dac47e9"));
		assertThat(hash3, is("89b69d0516f829893c696226650a8687"));
	}
	
	@Test
	public void testNestedJsonGeneration(){
		JSONObject startJson = JSON.make("top", JSON.make("untouched", "true"));
		JSON.putWithDotPath(startJson, "top.new", "here");
		JSON.putWithDotPath(startJson, "over_the_top", "yeah");
		JSON.putWithDotPath(startJson, "other.level1.level2a.level3.new", "deep down");
		JSON.putWithDotPath(startJson, "other.level1.level2b.level3.new", "deep down 2");
		//JSON.printJSONpretty(startJson);
		System.out.println(startJson);
		assertTrue(JSON.getJObject(startJson, ("other.level1.level2a").split("\\.")).containsKey("level3"));
	}
	
	@Test
	public void testPrivateNetworkCheck() throws Exception{
		boolean isPrivate;
		try {			isPrivate = Security.isPrivateNetwork(null);	assertTrue(false);
		}catch (Exception e){}
		isPrivate = Security.isPrivateNetwork("192.168.0.2");				assertTrue(isPrivate);
		isPrivate = Security.isPrivateNetwork("https://192.168.0.2");		assertTrue(isPrivate);
		isPrivate = Security.isPrivateNetwork("http://192.168.0.2:20721");	assertTrue(isPrivate);
		isPrivate = Security.isPrivateNetwork("193.0.0.2");					assertFalse(isPrivate);
		isPrivate = Security.isPrivateNetwork("https://193.0.0.2:20721");	assertFalse(isPrivate);
		isPrivate = Security.isPrivateNetwork("192.168.0.2:20721");			assertTrue(isPrivate);
		isPrivate = Security.isPrivateNetwork("localhost");					assertTrue(isPrivate);
		isPrivate = Security.isPrivateNetwork("http://172.31.255.255");		assertTrue(isPrivate);
		isPrivate = Security.isPrivateNetwork("http://172.32.255.255");		assertFalse(isPrivate);
		isPrivate = Security.isPrivateNetwork("http://localhost:20721");	assertTrue(isPrivate);
		isPrivate = Security.isPrivateNetwork("2001:db8:0:8d3:0:8a2e:70:7344");		assertFalse(isPrivate);
		//isPrivate = Security.isPrivateNetwork("fd9e:21a7:a92c:2323::2"); assertTrue(isPrivate); 	//what are private IPv6 addresses??
		isPrivate = Security.isPrivateNetwork("http://example.com/index.html");		assertFalse(isPrivate);
		isPrivate = Security.isPrivateNetwork("example.com");				assertFalse(isPrivate);
	}

}
