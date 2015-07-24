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
    protected final Iterable<String> compilerOptions;
    
    public ArtifactCompiler(Filer filer, List<File> classPath, List<File> sourcePath,
                            File classOutput, File sourceOutput, Iterable<String> compilerOptions)
                                                                        throws IOException
    {
        this.javaCompiler = ToolProvider.getSystemJavaCompiler();
        this.fileManager = FileManagerFactory.make(javaCompiler, classPath, sourcePath,
                                                   classOutput, sourceOutput);
        this.compilerOptions = compilerOptions;
    }
    
    
    public static ArtifactCompiler makeFromProcessorOptions(Filer filer,
                                                            ProcessorOptions processorOptions,
                                                            Iterable<String> compilerOptions)
                                                                                throws IOException
    {
        return new ArtifactCompiler(filer,
                                    processorOptions.effectiveClassPath,
                                    processorOptions.sourcePath,
                                    processorOptions.classOutput,
                                    processorOptions.sourceOutput,
                                    compilerOptions);
    }
    
    
    public void compileAll(Iterable<String> qualifiedJavaClasses) throws IOException
    {
        List<JavaFileObject> javaSourceFiles = new ArrayList<JavaFileObject>();
        for (String sourceClass : qualifiedJavaClasses)
        {
            JavaFileObject javaFile = fileManager.getJavaFileForInput(SOURCE_PATH, sourceClass,
                                                                      Kind.SOURCE);
            if (javaFile == null) {
                String msg = "Could not find a source file to compile: " + sourceClass;
                throw new IllegalArgumentException(msg);
            } else {
                javaSourceFiles.add(javaFile);
            }
        }

        if (javaSourceFiles.isEmpty()) {
            return;
        }

        CompilationTask task = javaCompiler.getTask(null, fileManager, null, compilerOptions,
                                                    null, javaSourceFiles);
        task.call();       
    }
}
