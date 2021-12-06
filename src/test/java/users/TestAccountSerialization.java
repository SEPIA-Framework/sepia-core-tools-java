package users;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

import net.b07z.sepia.server.core.data.Role;
import net.b07z.sepia.server.core.tools.JSON;
import net.b07z.sepia.server.core.users.Account;
import net.b07z.sepia.server.core.users.SharedAccessItem;

public class TestAccountSerialization {

	@Test
	public void test(){
		//test account
		JSONObject accountJson = JSON.make(
			"userId", "uid101",
			"email", "test@example.local",
			"phone", "12345",
			"accessLevel", -1,
			"userName", JSON.make("nick", "Testy"),
			"prefLanguage", "en"
		);
		JSON.put(accountJson, "userBirth", "2015.01.01");
		JSON.put(accountJson, "userRoles", JSON.makeArray(Role.user.name()));
		JSON.put(accountJson, "sharedAccess", JSON.make(
			"remoteActions", JSON.makeArray(
				new SharedAccessItem("uid202", "o1", JSON.make("type", "test")).toJson()
			)
		));
		
		Account account = new Account();
		account.importJSON(accountJson);
		
		assertTrue(account.getUserID().equals("uid101"));
		assertTrue(account.getEmail().equals("test@example.local"));
		assertTrue(account.getUserNameShort().equals("Testy"));
		assertTrue(account.getUserRoles().get(0).equals(Role.user.name()));
		assertTrue(account.getSharedAccess().get("remoteActions").get(0).getUser().equals("uid202"));
		
		JSONObject accExport = account.exportJSON();
		//System.out.println("accExport: " + accExport);
		assertTrue(accExport.size() == accountJson.size());
	}

}
