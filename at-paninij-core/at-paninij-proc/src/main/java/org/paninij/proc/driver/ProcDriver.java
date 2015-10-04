package org.paninij.proc.driver;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.lang.System.getProperty;
import static javax.tools.JavaFileObject.Kind.SOURCE;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;
import static javax.tools.StandardLocation.SOURCE_PATH;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;


public class ProcDriver
{
    public static class Settings
    {
        final Iterable<File> classPath;
        final Iterable<File> sourcePath;
        final File classOutput;
        final File sourceOutput;
        final Iterable<String> options;
        
        public Settings(Iterable<File> classPath, Iterable<File> sourcePath, File classOutput,
                        File sourceOutput, Iterable<String> options)
        {
            assert classPath != null
                && sourcePath != null
                && classOutput != null
                && sourceOutput != null
                && options != null;
            
            this.classPath = classPath;
            this.sourcePath = sourcePath;
            this.classOutput = classOutput;
            this.sourceOutput = sourceOutput;
            this.options = options;
        }
    }
    
    static final List<File> DEFAULT_CLASS_PATH;
    static final List<File> DEFAULT_SOURCE_PATH = asList(
        new File("src/main/java"),
        new File("src/main/at-paninij"),
        new File("target/generated-sources")
    );
    static final File DEFAULT_CLASS_OUTPUT = new File("target/classes");
    static final File DEFAULT_SOURCE_OUTPUT = new File("target/generated-sources");
    static final List<String> DEFAULT_OPTIONS = asList("-proc:only");
    
    public static final Settings DEFAULT_SETTINGS;

    static {
        List<String> classPath = asList(getProperty("java.class.path").split(File.pathSeparator));
        DEFAULT_CLASS_PATH = new ArrayList<File>();
        for (String s : classPath) {
            DEFAULT_CLASS_PATH.add(new File(s));
        }
        
        DEFAULT_SETTINGS = new Settings(DEFAULT_CLASS_PATH, DEFAULT_SOURCE_PATH,
                                        DEFAULT_CLASS_OUTPUT, DEFAULT_SOURCE_OUTPUT,
                                        DEFAULT_OPTIONS);
    }
    
    
    final Settings settings;
    final JavaCompiler javaCompiler;
    final StandardJavaFileManager fileManager;

    
    public ProcDriver(Settings settings) throws IOException
    {
        this.settings = settings;

        javaCompiler = ToolProvider.getSystemJavaCompiler();

        fileManager = javaCompiler.getStandardFileManager(null, null, null);
        fileManager.setLocation(CLASS_PATH, settings.classPath);
        fileManager.setLocation(SOURCE_PATH, settings.sourcePath);
        fileManager.setLocation(CLASS_OUTPUT, singleton(settings.classOutput));
        fileManager.setLocation(SOURCE_OUTPUT, singleton(settings.sourceOutput));
    }
    
    public void process(String... compilationUnits) throws IOException
    {
        CompilationTask task = javaCompiler.getTask(null, fileManager, null, settings.options, null,
                                                    lookupCompilationUnits(compilationUnits));
        task.call();
    }
    
    private List<JavaFileObject> lookupCompilationUnits(String... names) throws IOException
    {
        List<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>(names.length);
        for (String compilationUnit : names)
        {
            JavaFileObject template = fileManager.getJavaFileForInput(StandardLocation.SOURCE_PATH,
                                                                      compilationUnit, SOURCE);
            if (template == null) {
                String msg = "Could not load the template source file object: " + compilationUnit;
                throw new IllegalArgumentException(msg);
            }

            compilationUnits.add(template);
        }
        return compilationUnits;
    }
}