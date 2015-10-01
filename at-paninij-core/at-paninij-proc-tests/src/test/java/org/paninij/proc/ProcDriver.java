package org.paninij.proc;

import static javax.tools.JavaFileObject.Kind.SOURCE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.junit.Before;
import org.junit.Test;
import org.paninij.proc.util.FileManagerFactory;

/**
 * Includes tests which perform a Java compilation task with a `PaniniProcessor` instance configured
 * to use `-Apanini.staticOwnership=SOTER`.
 */
public class ProcDriver
{
    private static final String CLASS_PATH = System.getProperty("java.class.path");
    private static final String SOURCE_PATH = "src/main/java:target/generated-sources:src/test/java:target/generated-test-sources";
    private static final String CLASS_OUTPUT = "target/test-classes";
    private static final String SOURCE_OUTPUT = "target/generated-test-sources";
    private static final List<String> OPTIONS = Arrays.asList(new String[] {"-proc:only"});
    
    private JavaCompiler javaCompiler;
    private StandardJavaFileManager fileManager;
    private List<JavaFileObject> compilationUnits;

    @Before
    public void setUp() throws IOException
    {
        javaCompiler = ToolProvider.getSystemJavaCompiler();
        fileManager = FileManagerFactory.make(javaCompiler, CLASS_PATH, SOURCE_PATH,
                                              CLASS_OUTPUT, SOURCE_OUTPUT);
        compilationUnits = new ArrayList<JavaFileObject>();
    }
    
    @Test
    public void processNormalTemplate() throws IOException
    {
        processTemplate("org.paninij.proc.shapes.normal.NormalTemplate");
    }
    
    private void processTemplate(String templateName) throws IOException
    {
        JavaFileObject template = fileManager.getJavaFileForInput(StandardLocation.SOURCE_PATH,
                                                                  templateName, SOURCE);
        if (template == null) {
            String msg = "Could not load the template source file object: " + templateName;
            throw new IllegalArgumentException(msg);
        }

        compilationUnits.add(template);

        CompilationTask task = javaCompiler.getTask(null, fileManager, null, OPTIONS,
                                                    null, compilationUnits);
        task.call();
    }
}
