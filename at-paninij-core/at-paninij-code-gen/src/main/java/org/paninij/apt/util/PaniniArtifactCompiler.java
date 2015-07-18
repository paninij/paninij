package org.paninij.apt.util;

import static java.io.File.pathSeparator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;


/**
 * Uses an appropriately configured `JavaCompiler` and `StandardJavaFileManager` to compile
 * `@PaniniJ` source artifacts to Java class files.
 */
public class PaniniArtifactCompiler
{
    JavaCompiler javaCompiler;
    StandardJavaFileManager fileManager;
    
    public PaniniArtifactCompiler(String classPath, String sourcePath,
                                  String classOutput, String sourceOutput)
    {
        javaCompiler = ToolProvider.getSystemJavaCompiler();

        fileManager = javaCompiler.getStandardFileManager(null, null, null);
        if (fileManager == null) {
            throw new IllegalStateException("Could not get the standard file manager.");
        }

        initFileManagerLocations(fileManager, classPath, sourcePath, classOutput, sourceOutput);
    }
    
    
    public static void initFileManagerLocations(StandardJavaFileManager fileManager,
                                                String classPath, String sourcePath,
                                                String classOutput, String sourceOutput)
    {
        try {
            setFileManagerLocation(fileManager, StandardLocation.CLASS_PATH, classPath);
            setFileManagerLocation(fileManager, StandardLocation.SOURCE_PATH, sourcePath);
            setFileManagerLocation(fileManager, StandardLocation.CLASS_OUTPUT, classOutput);
            setFileManagerLocation(fileManager, StandardLocation.SOURCE_OUTPUT, sourceOutput);
        }
        catch (IOException ex)
        {
            String msg = "Could not fully initialize the file manager with the given arguments.";
            throw new IllegalArgumentException(msg);
        }
    }
    
    /**
     * @param fm     The file manager on which this location is set.
     * @param loc    The location with which the file paths are being associated.
     * @param path  A list of file paths separated by `File.pathSeparator` (i.e. ":" or ";").
     * 
     * @throws IOException If the given path was somehow invalid.
     */
    private static void setFileManagerLocation(StandardJavaFileManager fm, Location loc,
                                               String path) throws IOException
    {
        String[] strArr = path.split(File.pathSeparator);
        if (path.equals("") || strArr.length == 0)
        {
            String msg = "The given path cannot be used as a file manager location.";
            throw new IllegalArgumentException(msg);
        }

        File[] fileArr = new File[strArr.length];
        for (int idx = 0; idx < strArr.length; idx++)
        {
            File f = new File(strArr[idx]);
            fileArr[idx] = f;
        }
        fm.setLocation(loc, Arrays.asList(fileArr));
    }
    
    
    public static PaniniArtifactCompiler makeFromProcessorOptions(Map<String, String> options)
                                                                      throws IOException
    {
        String classPath = buildEffectiveClassPath(options.get("panini.classPath"),
                                                   options.get("panini.classPathFile"));
        String sourcePath = options.get("panini.sourcePath");
        String classOutput = options.get("panini.classOutput");
        String sourceOutput = options.get("panini.sourceOutput");

        return new PaniniArtifactCompiler(classPath, sourcePath, classOutput, sourceOutput);
    }
    

    /**
     * Appends the contents of the `classPathFile` to the given `classPath`. Either argument can
     * be `null`.
     * 
     * @throws IllegalArgumentException if the `classPathFile` could not be read.
     */
    public static String buildEffectiveClassPath(String classPath, String classPathFile)
    {
        if (classPath == null) {
            classPath = "";
        }
        if (classPathFile == null || classPathFile == "") {
            return classPath;
        }

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(classPathFile));
            String contents = new String(bytes, "UTF-8");;
            if (! contents.isEmpty()) {
                classPath = (classPath.equals("")) ? contents : classPath+pathSeparator+contents;
            }
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Could not read `classPathFile`: " + classPathFile);
        }

        return classPath;
    }


    public void compileAll(Iterable<String> qualifiedJavaClasses) throws IOException
    {
        List<JavaFileObject> javaFiles = new ArrayList<JavaFileObject>();
        for (String sourceClass : qualifiedJavaClasses)
        {
            JavaFileObject javaFile = fileManager.getJavaFileForInput(StandardLocation.SOURCE_PATH,
                                                                      sourceClass, Kind.SOURCE);
            if (javaFile == null) {
                String msg = "Could not find a source file to compile: " + sourceClass;
                throw new IllegalArgumentException(msg);
            } else {
                javaFiles.add(javaFile);
            }
        }

        if (javaFiles.isEmpty()) {
            return;
        }

        List<String> options = Arrays.asList(new String[] {"-proc:none"});
        CompilationTask task = javaCompiler.getTask(null, fileManager, null, options, null, javaFiles);
        task.call();       
    }
}
