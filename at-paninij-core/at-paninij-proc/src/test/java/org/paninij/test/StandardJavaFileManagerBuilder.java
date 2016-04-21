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
 *  Dr. Hridesh Rajan,
 *  Dalton Mills,
 *  David Johnston,
 *  Trey Erenberger
 *  Jackson Maddox
 *******************************************************************************/
package org.paninij.test;

import static java.util.Collections.singleton;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;
import static javax.tools.StandardLocation.SOURCE_PATH;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

//TODO: Consider allowing dependency injection in the form of a JavaCompiler
final public class StandardJavaFileManagerBuilder {
    
    private static final String TEMP_DIR_PREFIX = "java-compiler-runner";

    private File sourceOutput;
    private File classOutput;
    private List<File> sourcePath = new ArrayList<>();
    private List<File> classPath = new ArrayList<>();
    private boolean isBuilt;
    
    private StandardJavaFileManagerBuilder() { }
    
    public static StandardJavaFileManagerBuilder newBuilder() {
        return new StandardJavaFileManagerBuilder();
    }

    /**
     * <p>The given directory will be searched for class files during the compilation task.
     *
     * <p>By default, there are no directories (explicitly) on the class path.
     *
     * @return The receiver instance (i.e. {@code this}).
     */
    public StandardJavaFileManagerBuilder addClassPathDir(File dir) {
        assert dir != null;
        classPath.add(dir);
        return this;
    }

    /**
     * <p>All of the given directories will be searched for class files during the compilation task.
     *
     * <p>By default, there are no directories (explicitly) on the class path.
     *
     * @return The receiver instance (i.e. {@code this}).
     */
    public StandardJavaFileManagerBuilder addAllClassPathDirs(Iterable<File> dirs) {
        assert dirs != null;
        dirs.forEach(this::addClassPathDir);
        return this;
    }

    /**
     * <p>The given directory will be searched for source files during the compilation task.
     *
     * <p>By default, there are no directories (explicitly) on the source path.
     *
     * @return The receiver instance (i.e. {@code this}).
     */
    public StandardJavaFileManagerBuilder addSourcePathDir(File dir) {
        assert dir != null;
        sourcePath.add(dir);
        return this;
    }

    /**
     * <p>All of the given directories will be searched for source files during the compilation
     * task.
     *
     * <p>By default, there are no directories (explicitly) on the source path.
     *
     * @return The receiver instance (i.e. {@code this}).
     */
    public StandardJavaFileManagerBuilder addAllSourcePathDirs(Iterable<File> dirs) {
        assert dirs != null;
        dirs.forEach(this::addSourcePathDir);
        return this;
    }

    /**
     * <p>Class files created during the compilation task will be written to this directory.
     *
     * <p>By default, newly created class files will be written to a temporary directory.
     *
     * @return The receiver instance (i.e. {@code this}).
     */
    public StandardJavaFileManagerBuilder setClassOutputDir(File dir) {
        assert dir != null;
        classOutput = dir;
        return this;
    }

    /**
     * <p>Source files created during the compilation task will be written to this directory.
     *
     * <p>By default, newly created source files will be written to a temporary directory.
     *
     * @return The receiver instance (i.e. {@code this}).
     */
    public StandardJavaFileManagerBuilder setSourceOutputDir(File dir) {
        assert dir != null;
        sourceOutput = dir;
        return this;
    }
    
    public StandardJavaFileManager build() throws IOException {
        if (isBuilt) {
            String msg = "`StandardJavaFileManagerBuilder.build()` can only be called once.";
            throw new IllegalStateException(msg);
        } else {
            StandardJavaFileManager retVal = _build();
            _finish();
            return retVal;
        }
    }
    
    private StandardJavaFileManager _build() throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                null, // TODO: Support user-defined diagnostics listeners.
                null, 
                null);
        
        fileManager.setLocation(CLASS_PATH, classPath);
        fileManager.setLocation(SOURCE_PATH, sourcePath);
        fileManager.setLocation(CLASS_OUTPUT, singleton(classOutput == null ? tmp() : classOutput));
        fileManager.setLocation(SOURCE_OUTPUT, singleton(classOutput == null ? tmp() : sourceOutput));
        
        return fileManager;
    }
    
    /**
     * Returns a {@link File} handle to a newly created temporary directory which is writable.
     */
    private File tmp() throws IOException {
        return Files.createTempDirectory(TEMP_DIR_PREFIX).toFile();
    }
    
    /**
     * Sets `isBuilt` to true, and sets all fields to `null` for garbage collection.
     */
    private void _finish() {
        isBuilt = true;
        classPath = null;
        sourcePath = null;
        classOutput = null;
        sourceOutput = null;
    }
}
