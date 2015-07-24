package org.paninij.apt.soter;

import static javax.tools.JavaFileObject.Kind.SOURCE;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.junit.Before;
import org.junit.Test;
import org.paninij.apt.PaniniProcessor;
import org.paninij.apt.ProcessorOptions;
import org.paninij.apt.check.StaticOwnershipTransfer;
import org.paninij.apt.util.FileManagerFactory;
import org.paninij.runtime.check.DynamicOwnershipTransfer;

/**
 * Includes tests which perform a Java compilation task with a `PaniniProcessor` instance configured
 * to use `-Apanini.staticOwnership=SOTER`.
 */
public class TestPaniniProcessor
{
    private static final String CLASS_PATH_FILE = "target/generated-resources/maven/panini_processor_classpath.txt";
    private static final String CLASS_PATH = ProcessorOptions.makeEffectiveClassPathString("target/test-classes", CLASS_PATH_FILE);
    private static final String SOURCE_PATH = "src/test/java:target/generated-test-sources";
    private static final String CLASS_OUTPUT = "target/test-classes";
    private static final String SOURCE_OUTPUT = "target/generated-test-sources";
    
    private JavaCompiler javaCompiler;
    private StandardJavaFileManager fileManager;
    private List<Processor> processors;
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
    public void processActiveClientTemplate() throws IOException
    {
        processTemplate("org.paninij.apt.soter.ActiveClientTemplate");
    }
    
    @Test
    public void processLeakyServerTemplate() throws IOException
    {
        processTemplate("org.paninij.apt.soter.LeakyServerTemplate");
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

        CompilationTask task = javaCompiler.getTask(null, fileManager, null, makeOptionsList(),
                                                    null, compilationUnits);
        task.call();
    }
    
    private List<String> makeOptionsList()
    {
        List<String> options = new ArrayList<String>();
        options.add("-proc:only");
        options.add(makeOption("panini.classPath", CLASS_PATH));
        options.add(makeOption("panini.sourcePath", SOURCE_PATH));
        options.add(makeOption("panini.classOutput", CLASS_OUTPUT));
        options.add(makeOption("panini.sourceOutput", SOURCE_OUTPUT));
        options.add(makeOption("panini.ownershipTransfer.static", "SOTER"));
        return options;
    }
    
    private String makeOption(String key, String value)
    {
        return MessageFormat.format("-A{0}={1}", key, value);
    }
}
