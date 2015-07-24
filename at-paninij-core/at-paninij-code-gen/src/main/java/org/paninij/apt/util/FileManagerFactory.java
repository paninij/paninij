package org.paninij.apt.util;

import static javax.tools.StandardLocation.*;
import static org.paninij.apt.ProcessorOptions.makePathFromString;
import static org.paninij.apt.util.Collections.makeSingletonList;

import java.io.File;
import java.io.IOException;

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
        return make(javaCompiler, classPath, sourcePath, makeSingletonList(classOutput),
                    makeSingletonList(sourceOutput));
    }
}
