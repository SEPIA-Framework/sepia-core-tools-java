package net.b07z.sepia.server.core.tools;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Class loader to be used for plugins to make them run in a sandbox.
 * 
 * @author Florian Quirin
 *
 */
public class SandboxClassLoader extends URLClassLoader {
    //black-list classes
	private List<String> blackList;
	
	/**
	 * Create a sandboxed class loader for a jar-file or folder with a blacklist of classes
	 * and packages that are restricted.
	 * @param fileOrDir - jar-file or directory
	 * @param blackList - list of class names and packages that this class loader is not allowed to open
	 * @throws MalformedURLException
	 */
    public SandboxClassLoader(File fileOrDir, List<String> blackList) throws MalformedURLException {
        super(new URL[]{fileOrDir.toURI().toURL()});
        this.blackList = blackList;
    }
    
    @Override
    /**
     * Loads class with given binary name if not done before and not on blacklist of the sand-box.
     */
    public Class<?> loadClass(String name) throws ClassNotFoundException {
    	//TODO: add a white-list/black-list combo here? Maybe check for package on black-list first then white-list
    	if (name.contains(".")){
    		String parent = name.substring(0, name.lastIndexOf('.'));
    		if (blackList.contains(parent)){
    			throw new RuntimeException("access denied (" + parent + ")");
    		}
    	}
    	if (blackList.contains(name)){
			throw new RuntimeException("access denied (" + name + ")");
		}
    	return super.loadClass(name);
    }
}
