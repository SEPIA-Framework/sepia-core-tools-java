package net.b07z.sepia.server.core.users;

import java.util.ArrayList;
import java.util.List;

import net.b07z.sepia.server.core.data.Role;
import net.b07z.sepia.server.core.server.RequestParameters;

/**
 * A local fake account, only for testing.
 */
public class LocalTestAccount extends Account {

	private final List<String> userRoles = new ArrayList<>();			//user roles managing certain access rights

	private String userId = "fakeUser@example.com";
	
	public LocalTestAccount() {
		userRoles.add(Role.user.name()); 		//all users should have this, the rest should be set in JUnit tests
	}
	
	public LocalTestAccount(String userId) {
		this.userId = userId; 
	}

	@Override
	public String getUserID() {
		return userId;
	}
	public void setUserID(String id) {
		userId = id;
	}

	@Override
	public int getAccessLevel() {
		return 0;
	}

	@Override
	public boolean authenticate(RequestParameters params) {
		return true;
	}
	
	@Override
	public boolean hasRole(String roleName){
		return userRoles != null && userRoles.contains(roleName);
	}
	public void setUserRole(String... roleNames){
		for (String r : roleNames){
			userRoles.add(r);
		}
	}
}
