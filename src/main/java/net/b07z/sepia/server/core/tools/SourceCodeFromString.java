package net.b07z.sepia.server.core.tools;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * Class used during on-the-fly compiling of a source-code string. 
 * 
 * @author Florian Quirin
 */
public class SourceCodeFromString extends SimpleJavaFileObject {
    final String code;

    /**
     * Construct the new source by giving the (full) class name and code content.
     * @param name - full class name, e.g. com.example.my_package.MyNewClass
     * @param code - Java source-code as seen in .java files (including package and import sections)
     */
    public SourceCodeFromString(String name, String code){
        super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }
    
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors){
        return code;
    }
}
