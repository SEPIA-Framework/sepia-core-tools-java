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
	
	@Test
	public void testRandomUserPassword() throws Exception {
		//test random user password
		String pwd1 = Security.createRandomString(16, null);
		String pwd2 = Security.createRandomString(32, "ABCDabcd01234!_");
		String pwd3 = Security.createRandomString(24, Security.simplePasswdChars);
		String pwd4 = Security.createRandomString(16, Security.simplePasswdChars) + Security.createRandomString(8, Security.charsSimpleSpecials);
		String pwd5 = "AAAAAA111";
		String pwd5Shuffle = Security.shuffleString(pwd5);
		assertTrue(pwd1.length() == 16);
		assertTrue(pwd2.length() == 32);
		assertTrue(pwd3.length() == 24);
		assertTrue(pwd4.length() == 24);
		assertTrue(pwd5Shuffle.length() == pwd5.length());
		assertTrue(!pwd5Shuffle.equals(pwd5));
		assertTrue(pwd5Shuffle.replaceAll("A", "").equals("111"));
		assertTrue(pwd5Shuffle.replaceAll("1", "").equals("AAAAAA"));
		/*
		System.out.println("passwd1: " + pwd1);
		System.out.println("passwd2: " + pwd2);
		System.out.println("passwd3: " + pwd3);
		System.out.println("passwd4: " + pwd4);
		System.out.println("passwd5: " + pwd5Shuffle);
		for (int i=0; i<122; i++){
			System.out.println(i + ": " + Character.toString(i));
		}
		*/
	}

}
