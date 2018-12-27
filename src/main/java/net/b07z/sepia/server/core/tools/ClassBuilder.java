package net.b07z.sepia.server.core.tools;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Build classes from strings. Very handy for configuration files and plug-ins.
 * 
 * @author Florian Quirin
 *
 */
public class ClassBuilder {

	/**
	 * Constructs a new instance of a class with arguments (if available) just by using the name of the class.<br>
	 * NOTE! Because getClass always returns non-primitives like "Integer" instead of "int" but they are rarely used, all
	 * classes are cast to primitives if possible. That means the constructor will not be found if it has "Integer" instead of "int".
	 * Same is most likely true for the constructors containing the class "Object".
	 * @param classLoader - loader that has access to the class. Use null or 'ClassLoader.getSystemClassLoader()' for default.
	 * @param canonicalClassName - canonical name of the class, e.g.: "java.lang.String"
	 * @param arguments - arguments usually passed to the constructor (optional).
	 * @return constructed class
	 */
	public static Object construct(ClassLoader classLoader, String canonicalClassName, Object... arguments){
		try{
			if (classLoader == null){
				classLoader = ClassLoader.getSystemClassLoader();
			}
			Object clazz;
			if (arguments != null && arguments.length > 0){
				Class<?>[] arg_clazzes = new Class[arguments.length];
				for (int i=0; i<arguments.length; i++){
					arg_clazzes[i] = arguments[i].getClass();
					if (arg_clazzes[i].isInstance(String.class)){
						continue;
					}else if (arg_clazzes[i].equals(Integer.class)){
						arg_clazzes[i] = Integer.TYPE;
					}else if (arg_clazzes[i].equals(Boolean.class)){
						arg_clazzes[i] = Boolean.TYPE;
					}else if (arg_clazzes[i].equals(Double.class)){
						arg_clazzes[i] = Double.TYPE;
					}else if (arg_clazzes[i].equals(Long.class)){
						arg_clazzes[i] = Long.TYPE;
					}else if (arg_clazzes[i].equals(Byte.class)){
						arg_clazzes[i] = Byte.TYPE;
					}else if (arg_clazzes[i].equals(Float.class)){
						arg_clazzes[i] = Float.TYPE;
					}else if (arg_clazzes[i].equals(Short.class)){
						arg_clazzes[i] = Short.TYPE;
					}
				}
				Constructor<?> conztructor = Class.forName(canonicalClassName, true, classLoader).getConstructor(arg_clazzes);
				clazz = conztructor.newInstance(arguments);
			}else{
				Constructor<?> conztructor = Class.forName(canonicalClassName, true, classLoader).getConstructor();
				clazz = conztructor.newInstance();
			}
			return clazz;
			
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(DateTime.getLogDate() + " ERROR - Class not found: " + canonicalClassName, e);
		}
	}

	/**
	 * Constructs a new instance of a class just by using the name of the class.<br>
	 * 
	 * @param canonicalClassName - canonical name of the class, e.g.: "java.lang.String"
	 * @return constructed class
	 */
	public static Object construct(String canonicalClassName){
		return construct(null, canonicalClassName);
	}
	
	/**
	 * Experimental string source-code compiler.
	 * @param className - full class name including package, e.g. com.example.my_package.MyNewClass
	 * @param classCode - source-code as seen in Java files
	 * @param targetFolder - parent directory of compiled class file (without package-path) or null
	 */
	public static void compile(String className, String classCode, File targetFolder){
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null){
			String msg = "Cannot find Java compiler! "
					+ "Please upgrade from JRE to JDK or check JAVA_HOME: " + System.getProperty("java.home");
			//Debugger.println(msg, 1);
			throw new RuntimeException(msg);
		}
	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	    
	    List<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>();
        JavaFileObject file = new SourceCodeFromString(className, classCode);
        compilationUnits.add(file);
        
        // This sets up the class path that the compiler will use.
        // I've added the .jar file that contains the DoStuff interface within in it...
        List<String> optionList = new ArrayList<>();
        String folderOrMemory = "MEMORY ONLY";
        if (targetFolder != null){
        	folderOrMemory = targetFolder.getAbsolutePath().toString();
        	optionList.add("-d");
        	optionList.add(folderOrMemory);
        }
        /*
        optionList.add("-classpath");
        optionList.add(System.getProperty("java.class.path") + ";dist/InlineCompiler.jar"); 	//example for classpath
        */
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

	    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null, compilationUnits);
	    if (task.call()){
	    	//Done
	    	Debugger.println("Compiled '" + className + "' to '" + folderOrMemory, 3);
	    }else{
	    	//Error(s)
	    	for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()){
	    		Debugger.println("ClassBuilder - compiling of '" + diagnostic.getSource().toUri() + "' failed. Error: \n" +
	    				"Line " + diagnostic.getLineNumber() + ": " + diagnostic.getMessage(Locale.ENGLISH), 1);
            }
	    }
	}

}
