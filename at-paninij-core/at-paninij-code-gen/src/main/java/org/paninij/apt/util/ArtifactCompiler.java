package org.paninij.apt.util;

import static javax.tools.StandardLocation.SOURCE_PATH;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    protected final Filer filer;
    protected final JavaCompiler javaCompiler;
    protected final StandardJavaFileManager fileManager;
    protected final Iterable<String> compilerOptions;
    
    protected final Set<String> uncompiledArtifacts = new HashSet<String>();
    
    public ArtifactCompiler(Filer filer, List<File> classPath, List<File> sourcePath,
                            File classOutput, File sourceOutput, Iterable<String> compilerOptions)
                                                                        throws IOException
    {
        this.filer = filer;
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
    
    
    public void compileArtifacts()
    {
        List<JavaFileObject> javaSourceFiles = new ArrayList<JavaFileObject>();
        for (String sourceClass : uncompiledArtifacts)
        {
            JavaFileObject javaSourceFile;
            try {
                javaSourceFile = fileManager.getJavaFileForInput(SOURCE_PATH, sourceClass, Kind.SOURCE);
            }
            catch (IOException ex)
            {
                String msg = "The file manager failed to find a Java source file: " + ex;
                throw new RuntimeException(msg, ex);
            }

            if (javaSourceFile == null)
            {
                String msg = "Could not find a source file to compile: " + sourceClass;
                throw new IllegalArgumentException(msg);
            }
            javaSourceFiles.add(javaSourceFile);
        }

        if (javaSourceFiles.isEmpty()) {
            return;
        }

        CompilationTask task = javaCompiler.getTask(null, fileManager, null, compilerOptions,
                                                    null, javaSourceFiles);
        task.call();       
        
        // Reset the set of artifacts.
        uncompiledArtifacts.clear();
    }
    
    
    /**
     * @param cls The fully qualified name of the class that will go in the newly created file.
     * @param src The source to be put in the newly create java file.
     */
    private void createJavaFile(Artifact artifact)
    {
        try {
            JavaFileObject file = filer.createSourceFile(artifact.getQualifiedName());
            file.openWriter().append(artifact.getContent()).close();
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to create a Java source file: " + ex, ex);
        }
    }

    
    public void file(Artifact artifact)
    {
        if (artifact == null) {
            return;
        } else {
            uncompiledArtifacts.add(artifact.getQualifiedName());
            createJavaFile(artifact);
        }
    }
    
    
    public void fileAll(Iterable<Artifact> artifacts)
    {
        for (Artifact a : artifacts) {
            file(a);
        }
    }
    
    public void add(String qualifiedName)
    {
        uncompiledArtifacts.add(qualifiedName);
    }
}
