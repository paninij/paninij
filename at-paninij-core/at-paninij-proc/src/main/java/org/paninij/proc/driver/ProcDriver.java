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
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;

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
        public Iterable<File> classPath;
        public Iterable<File> sourcePath;
        public File classOutput;
        public File sourceOutput;
        public Iterable<String> options;
        
        public Settings(Iterable<File> classPath, Iterable<File> sourcePath, File classOutput,
                        File sourceOutput, Iterable<String> options)
        {
            this.classPath = classPath;
            this.sourcePath = sourcePath;
            this.classOutput = classOutput;
            this.sourceOutput = sourceOutput;
            this.options = options;
        }
        
        public boolean isWellFormed()
        {
            return classPath != null
                && sourcePath != null
                && classOutput != null
                && sourceOutput != null
                && options != null;
        }
    }
    
    public static Settings makeDefaultSettings()
    {
        List<File> classPath = new ArrayList<File>();
        for (String s : asList(getProperty("java.class.path").split(File.pathSeparator))) {
            classPath.add(new File(s));
        }
        List<File> sourcePath = asList(
            new File("src/main/java"),
            new File("src/main/at-paninij"),
            new File("target/generated-sources")
        );
        File sourceOutput = new File("target/generated-sources");
        File classOutput = new File("target/classes");
        List<String> options = asList("-proc:only", "-Apanini.exceptOnFailedChecks");

        return new Settings(classPath, sourcePath, classOutput, sourceOutput, options);
    }
    
    
    protected final Settings settings;
    protected final JavaCompiler javaCompiler;
    protected final StandardJavaFileManager fileManager;

    
    public ProcDriver(Settings settings) throws IOException
    {
        if (!settings.isWellFormed()) {
            throw new IllegalArgumentException("The given settings were not well-formed.");
        }

        this.settings = settings;

        javaCompiler = ToolProvider.getSystemJavaCompiler();

        fileManager = javaCompiler.getStandardFileManager(null, null, null);
        fileManager.setLocation(CLASS_PATH, settings.classPath);
        fileManager.setLocation(SOURCE_PATH, settings.sourcePath);
        
        ensureDirExists(settings.classOutput);
        fileManager.setLocation(CLASS_OUTPUT, singleton(settings.classOutput));

        ensureDirExists(settings.sourceOutput);
        fileManager.setLocation(SOURCE_OUTPUT, singleton(settings.sourceOutput));
    }
    
    public void process(List<String> compilationUnits) throws IOException
    {
        String[] arr = new String[compilationUnits.size()];
        arr = compilationUnits.toArray(arr);
        process(arr);
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
    
    private static void ensureDirExists(File dir) throws IOException
    {
        if (dir.mkdir() == false && dir.isDirectory() == false) {
            throw new IOException("Could not ensure that the directory exists: " + dir);
        }
    }
}