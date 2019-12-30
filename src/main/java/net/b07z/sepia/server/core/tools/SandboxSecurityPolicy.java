package net.b07z.sepia.server.core.tools;

import java.net.SocketPermission;
import java.security.AllPermission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;

/**
 * TODO: add more info about the policy.
 * 
 * @author Florian Quirin
 *
 */
public class SandboxSecurityPolicy extends Policy{
    
    @Override
    public PermissionCollection getPermissions(ProtectionDomain domain){
    	if (isSandboxed(domain)){
            return sandboxPermissions();
        }else{
            return applicationPermissions();
        }        
    }
    
    /*
    public boolean implies(ProtectionDomain domain, Permission permission){
    	if (isSandboxed(domain)){
    		System.out.println("sandbox permissions request: " + permission.toString()); 		//debug
        }
    	return super.implies(domain, permission);
    }
    */
 
    private boolean isSandboxed(ProtectionDomain domain){
        return domain.getClassLoader() instanceof SandboxClassLoader;
    }
 
    private PermissionCollection sandboxPermissions(){
        Permissions permissions = new Permissions(); 
        //TODO: add more permissions?
        permissions.add(new SocketPermission("*", "connect"));
        permissions.add(new RuntimePermission("modifyThread"));
        return permissions;
    }
 
    private PermissionCollection applicationPermissions(){
        Permissions permissions = new Permissions();
        permissions.add(new AllPermission());
        return permissions;
    }
}
