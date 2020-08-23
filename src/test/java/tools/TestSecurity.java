package tools;

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import net.b07z.sepia.server.core.tools.Security;

public class TestSecurity {

	@Test
	public void test() throws Exception {
		//test basic conversion
		String testStr = Security.bytearrayToHexString(Security.getRandomSalt(32));
		byte[] ba = Hex.decodeHex(testStr.toCharArray());
		String hs = Security.bytearrayToHexString(ba);
		assertTrue(testStr.equals(hs));
		
		String testPwd = "my-supersafe-password-123";
		byte[] randomSalt = Security.getRandomSalt(32);
		int iterations = 20000;
		byte[] safeHashBytes = Security.getEncryptedPassword(testPwd, randomSalt, iterations, 32);
		String safeHash = Security.bytearrayToHexString(safeHashBytes);
		String clientSimpleHash = Security.hashClientPassword(testPwd);
		assertTrue(safeHash.length() == 64);
		assertTrue(clientSimpleHash.length() > 0);
	}

}
