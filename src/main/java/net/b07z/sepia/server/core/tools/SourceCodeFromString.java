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

    public SourceCodeFromString(String name, String code){
        super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }
    
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors){
        return code;
    }
}
