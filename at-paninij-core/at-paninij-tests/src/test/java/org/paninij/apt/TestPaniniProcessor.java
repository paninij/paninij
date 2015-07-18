package org.paninij.apt;

import static javax.tools.JavaFileObject.Kind.SOURCE;
import static org.paninij.apt.util.PaniniArtifactCompiler.buildEffectiveClassPath;
import static org.paninij.apt.util.PaniniArtifactCompiler.initFileManagerLocations;

import java.io.IOException;
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

public class TestPaniniProcessor
{
    private static final String CLASS_PATH_FILE = "target/generated-resources/maven/panini_processor_classpath.txt";
    private static final String CLASS_PATH = buildEffectiveClassPath("target/test-classes", CLASS_PATH_FILE);
    private static final String SOURCE_PATH = "src/test/java:target/generated-test-sources";
    private static final String CLASS_OUTPUT = "target/test-classes";
    private static final String SOURCE_OUTPUT = "target/generated-test-sources";
    
    private static final String HELLO_WORLD_TEMPLATE = "org.paninij.apt.HelloWorldTemplate";
    
    private JavaCompiler javaCompiler;
    private StandardJavaFileManager fileManager;
    private List<Processor> processors;
    private List<JavaFileObject> compilationUnits;

    @Before
    public void setUp()
    {
        javaCompiler = ToolProvider.getSystemJavaCompiler();
        fileManager = javaCompiler.getStandardFileManager(null, null, null);
        initFileManagerLocations(fileManager, CLASS_PATH, SOURCE_PATH, CLASS_OUTPUT, SOURCE_OUTPUT);

        processors = new ArrayList<Processor>();
        PaniniProcessor p = new PaniniProcessor();
        p.initWithOptions(makeOptions());
        processors.add(p);

        compilationUnits = new ArrayList<JavaFileObject>();
    }
    
    private Map<String, String> makeOptions()
    {
        Map<String, String> options = new HashMap<String, String>();
        options.put("panini.dynamicOwnership", "NONE");
        options.put("panini.staticOwnership", "SOTER");
        options.put("panini.classPath",    CLASS_PATH);
        options.put("panini.sourcePath",   SOURCE_PATH);
        options.put("panini.classOutput",  CLASS_OUTPUT);
        options.put("panini.sourceOutput", SOURCE_OUTPUT);
        return options;
    }

    @Test
    public void processHelloWorldTemplate() throws IOException
    {
        JavaFileObject template = fileManager.getJavaFileForInput(StandardLocation.SOURCE_PATH,
                                                                  HELLO_WORLD_TEMPLATE, SOURCE);
        compilationUnits.add(template);

        CompilationTask task = javaCompiler.getTask(null, fileManager, null, null, null,
                                                    compilationUnits);
        task.setProcessors(processors);
        task.call();
    }
}
