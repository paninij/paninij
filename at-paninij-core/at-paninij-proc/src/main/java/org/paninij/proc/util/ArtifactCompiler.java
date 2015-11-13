/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package org.paninij.proc.util;

import static java.util.Collections.singleton;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;
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


/**
 * Uses an appropriately configured `JavaCompiler` and `StandardJavaFileManager` to compile
 * `@PaniniJ` source artifacts to Java class files.
 * 
 * TODO: Is this class still needed?
 */
public class ArtifactCompiler implements ArtifactMaker
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

        this.fileManager = this.javaCompiler.getStandardFileManager(null, null, null);
        fileManager.setLocation(CLASS_PATH, classPath);
        fileManager.setLocation(SOURCE_PATH, sourcePath);
        fileManager.setLocation(CLASS_OUTPUT, singleton(classOutput));
        fileManager.setLocation(SOURCE_OUTPUT, singleton(sourceOutput));

        this.compilerOptions = compilerOptions;
    }
    
    
    @Override
    public void add(Artifact artifact)
    {
        if (artifact == null) {
            return;
        } else {
            uncompiledArtifacts.add(artifact.getQualifiedName());
            if (artifact instanceof UserArtifact == false)
                createJavaSourceFile(artifact);
        }
    }

    @Override
    public void makeAll()
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
        if (task.call() == false) {
            throw new RuntimeException("Compilation subtask failed.");
        }
        
        // Reset the set of artifacts.
        uncompiledArtifacts.clear();
    }
    
    @Override
    public void close()
    {
        try {
            fileManager.flush();
            fileManager.close();
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to close a `ArtifactCompiler`'s `FileManager`.", ex);
        }
    }
   
    /**
     * @param cls The fully qualified name of the class that will go in the newly created file.
     * @param src The source to be put in the newly create java file.
     */
    private void createJavaSourceFile(Artifact artifact)
    {
        try {
            JavaFileObject file = filer.createSourceFile(artifact.getQualifiedName());
            file.openWriter().append(artifact.getContent()).close();
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to create a Java source file: " + ex, ex);
        }
    }
}
