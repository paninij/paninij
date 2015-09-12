package org.paninij.proc.util;

import static java.util.Collections.singleton;
import static javax.tools.StandardLocation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;

public class FileManagerFactory
{
    public static StandardJavaFileManager make(JavaCompiler javaCompiler,
                                               String classPath,
                                               String sourcePath,
                                               String classOutput,
                                               String sourceOutput) throws IOException
    {
        return make(javaCompiler,
                    makePathFromString(classPath),
                    makePathFromString(sourcePath),
                    makePathFromString(classOutput),
                    makePathFromString(sourceOutput));
    }
    

    public static StandardJavaFileManager make(JavaCompiler javaCompiler,
                                               Iterable<File> classPath,
                                               Iterable<File> sourcePath,
                                               Iterable<File> classOutput,
                                               Iterable<File> sourceOutput) throws IOException
    {
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        
        fileManager.setLocation(CLASS_PATH, classPath);
        fileManager.setLocation(SOURCE_PATH, sourcePath);
        fileManager.setLocation(CLASS_OUTPUT, classOutput);
        fileManager.setLocation(SOURCE_OUTPUT, sourceOutput);
        
        return fileManager;
    }


    public static StandardJavaFileManager make(JavaCompiler javaCompiler,
                                               Iterable<File> classPath,
                                               Iterable<File> sourcePath,
                                               File classOutput,
                                               File sourceOutput) throws IOException
    {
        return make(javaCompiler, classPath, sourcePath, singleton(classOutput),
                    singleton(sourceOutput));
    }
    
    
    public static StandardJavaFileManager assignDefaults(StandardJavaFileManager fileManager)
    {
        throw new UnsupportedOperationException("TODO!");
    }
    
    
    private static List<File> makePathFromString(String str)
    {
        if (str == null) {
            return null;
        }
    
        String[] strArray = str.split(File.pathSeparator);
        if (strArray.length == 0)
        {
            String msg = "The given `str` cannot be interpreted as a path: " + str;
            throw new IllegalArgumentException(msg);
        }
    
        List<File> path = new ArrayList<File>(strArray.length);
        for (String elem : strArray)
        {
            path.add(new File(elem));
        }
        return path;
    }
}
