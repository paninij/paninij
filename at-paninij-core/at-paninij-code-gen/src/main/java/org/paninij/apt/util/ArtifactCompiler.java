package org.paninij.apt.util;

import static javax.tools.StandardLocation.CLASS_PATH;
import static javax.tools.StandardLocation.SOURCE_PATH;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.paninij.apt.ProcessorOptions;


/**
 * Uses an appropriately configured `JavaCompiler` and `StandardJavaFileManager` to compile
 * `@PaniniJ` source artifacts to Java class files.
 */
public class ArtifactCompiler
{
    protected final JavaCompiler javaCompiler;
    protected final StandardJavaFileManager fileManager;
    
    public ArtifactCompiler(Filer filer, List<File> classPath, List<File> sourcePath,
                            File classOutput, File sourceOutput) throws IOException
    {
        javaCompiler = ToolProvider.getSystemJavaCompiler();

        fileManager = javaCompiler.getStandardFileManager(null, null, null);
        if (fileManager == null) {
            throw new IllegalStateException("Could not get the standard file manager.");
        }

        fileManager.setLocation(CLASS_PATH, classPath);
        fileManager.setLocation(SOURCE_PATH, sourcePath);
        fileManager.setLocation(CLASS_OUTPUT, makeSingletonList(classOutput));
        fileManager.setLocation(SOURCE_OUTPUT, makeSingletonList(sourceOutput));
    }
    
    
    private static <T> List<T> makeSingletonList(T elem)
    {
        ArrayList<T> list = new ArrayList<T>(1);
        list.add(elem);
        return list;
    }
    
    
    public static ArtifactCompiler makeFromProcessorOptions(Filer filer, ProcessorOptions options)
                                                                                throws IOException
    {
        return new ArtifactCompiler(filer, options.effectiveClassPath, options.sourcePath,
                                    options.classOutput, options.sourceOutput);
    }
    
    
    public void compileAll(Iterable<String> qualifiedJavaClasses) throws IOException
    {
        List<JavaFileObject> javaFiles = new ArrayList<JavaFileObject>();
        for (String sourceClass : qualifiedJavaClasses)
        {
            JavaFileObject javaFile = fileManager.getJavaFileForInput(SOURCE_PATH, sourceClass,
                                                                      Kind.SOURCE);
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
