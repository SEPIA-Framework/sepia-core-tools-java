package tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import net.b07z.sepia.server.core.tools.ClassBuilder;
import net.b07z.sepia.server.core.tools.SandboxClassLoader;

public class Test_ClassBuilder {

	public static void main(String[] args) throws ClassNotFoundException, 
											NoSuchMethodException, SecurityException, IllegalAccessException, 
											IllegalArgumentException, InvocationTargetException, IOException {
		//Test compile a string into SDK services package:
		
		//Define SDK services base:
		String basePackage = "net.b07z.sepia.sdk.services";
		
		//User that submitted code:
		String userId = "uid1006"; 		//this ID usually does not exist
		
		//Create services folders
		String baseFolder = "TestData/compilerTests/"; 		//we give this to the class loader later
		File destinationFolder = new File((baseFolder + basePackage + "/" + userId + "/").replaceAll("\\.", "/"));
		destinationFolder.mkdirs();
		
		//Source-code:
		String classPackage = basePackage + "." + userId;
		String classNameSimple = "HelloWorld";
		String className = classPackage + "." + classNameSimple;		
		String sourceCode = ""
			+ "package " + classPackage + ";"						+ "\n"
				+ "public class " + classNameSimple + " {" 			+ "\n"
					+ "public static void print(){"					+ "\n"
						+ "System.out.println(\"Hello World\");" 	+ "\n"
					+ "}" 											+ "\n"
					+ "public String toString(){"					+ "\n"
						+ "return \"Hello World\";" 				+ "\n"
					+ "}" 											+ "\n"
				+ "}" 												+ "\n"
			+ "";
		//System.out.println(sourceCode);
		
		//Compile
		ClassBuilder.compile(className, sourceCode, new File(baseFolder));
		
		//Test (static method)
		SandboxClassLoader classLoader = new SandboxClassLoader(new File(baseFolder), new ArrayList<>());
		Class<?> loadedClass = classLoader.loadClass(className);
		System.out.print("Invoking 'HelloWorld.print()' - Result: ");
		loadedClass.getMethod("print").invoke(null); 	//Note: static method needs no instance and no invoke object
		classLoader.close();
		
		//Test alternative constructor (non-static method):
		Object loadedClassV2 = ClassBuilder.construct(classLoader, className);
		System.out.println("Invoking 'HelloWorld.toString()' v2 - Result: " + loadedClassV2.toString());
	}

}
