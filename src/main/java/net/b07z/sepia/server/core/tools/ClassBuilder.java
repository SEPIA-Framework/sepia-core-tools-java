package net.b07z.sepia.server.core.tools;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
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
	 * 
	 * @param package_name - name of the package, e.g. "java.util". Can be empty.
	 * @param module_name - name of the class inside the package as string, e.g.: "Date"
	 * @param arguments - arguments usually passed to the constructor
	 * @return constructed class
	 */
	public static Object construct(String package_name, String module_name, Object... arguments){
		if (!package_name.isEmpty()){
			module_name = package_name + "." + module_name;		//package_name and module_name are separated in case the package_name changes
		}
		try{
			Object clazz;
			if (arguments.length > 0){
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
				Constructor<?> conztructor = Class.forName(module_name).getConstructor(arg_clazzes);
				clazz = conztructor.newInstance(arguments);
			}else{
				Constructor<?> conztructor = Class.forName(module_name).getConstructor();
				clazz = conztructor.newInstance();
			}
			return clazz;
			
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(DateTime.getLogDate() + " ERROR - Class not found: " + module_name, e);
		}
	}

	/**
	 * Constructs a new instance of a class just by using the name of the class.<br>
	 * 
	 * @param module_name - name of the class inside the package as string, e.g.: "Date"
	 * @return constructed class
	 */
	public static Object construct(String module_name){
		try{
			Object clazz;
			Constructor<?> conztructor = Class.forName(module_name).getConstructor();
			clazz = conztructor.newInstance();
			return clazz;
			
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(DateTime.getLogDate() + " ERROR - Class not found: " + module_name, e);
		}
	}
	
	/**
	 * Experimental string source-code compiler.
	 * @param className
	 * @param classCode
	 * @param fileName
	 */
	public static void compile(String className, String classCode, String fileName){
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	    
	    List<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>();
        JavaFileObject file = new SourceCodeFromString(className, classCode);
        compilationUnits.add(file);

	    JavaCompiler.CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);
	    System.out.println(task.call() + diagnostics.getDiagnostics().toString()); //passes every time
	}

}
