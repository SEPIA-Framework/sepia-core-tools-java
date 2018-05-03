package net.b07z.sepia.server.core.tools;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Custom Network Authenticator to login to secured areas on web servers etc.
 *  
 * @author Florian Quirin
 *
 */
public class NetAuthenticator extends Authenticator {
	
	private String username = "test";
	private String password = "1234";
	
	public NetAuthenticator(String username, String password){
		this.username = username;
		this.password = password;
	}
	public NetAuthenticator(){}
	
    protected PasswordAuthentication getPasswordAuthentication() {
        String username = this.username;
        String password = this.password;
        
        return new PasswordAuthentication(username, password.toCharArray());
    }
    
    //------
    
    /**
     * Sets this authenticator as the default Authenticator to be used by the 
     * networking code when a proxy or an HTTP server asks for authentication.<br>
     * NOTE: this is a global setting that applies to any auth. request!
     * @param username
     * @param password
     */
    public static void setAsDefault(String username, String password){
    	Authenticator.setDefault(new NetAuthenticator(username, password));
    }
}
