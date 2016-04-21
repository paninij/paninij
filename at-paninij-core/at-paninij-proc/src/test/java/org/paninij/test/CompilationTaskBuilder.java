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

import com.sun.source.tree.CompilationUnitTree;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static javax.tools.ToolProvider.getSystemJavaCompiler;

/**
 * <p>A builder for (un-called) instances of {@link JavaCompiler.CompilationTask} to simplify
 * correct usage of the {@link JavaCompiler} API.
 *
 * <p>The builder has methods of the form `set*()`, `add*()`, and `addAll()` in order to configure
 * the compilation task as desired. These methods all return the builder receiver instance on which
 * the method was called (i.e. `this`) to enable method-chaining. Once the appropriate methods are
 * called, then {@link #build()} is called in order to obtain the desired compilation task. The
 * compilation task returned from {@link #build()} will not have been called. The {@link #build()}
 * method can only be called once.
 *
 * <p>In the case that a some relevant aspect of the compilation task is not explicitly set via the
 * builder, the builder has been designed to use (hopefully) sensible defaults. For example, if
 * a compiler is not explicitly set, then {@link ToolProvider#getSystemJavaCompiler()} is used by
 * default. See the relevant method for information on default behavior. (E.g. The default
 * behavior regarding the compiler is described in {@link #setCompiler(JavaCompiler)}.)
 *
 * @author dwtj
 */
// TODO: Let a single builder create multiple `CompilationTask` instances.
// TODO: Use diagnostics listener.
// TODO: Support passing in names of classes to `compiler.getTask()`.
// TODO: Consider allowing dependency injection in the form of a JavaCompiler
final public class CompilationTaskBuilder {

    private CompilationTaskBuilder() { }

    public static CompilationTaskBuilder newBuilder() {
        return new CompilationTaskBuilder();
    }

    private boolean isBuilt = false;

    private StandardJavaFileManager fileManager;
    private List<Processor> processors = new ArrayList<>();
    private List<JavaFileObject> compilationUnits = new ArrayList<>();
    private List<String> options = new ArrayList<>();
    private List<String> classes = new ArrayList<>();
    private DiagnosticListener<? super JavaFileObject> diagListener;

    /**
     * <p>Builds and returns a {@link CompilationTask} whose properties conform to prior calls of
     * the builder's methods. This can only be called once.
     *
     * @throws IllegalStateException
     *            If this method has been called before.
     * @throws IllegalStateException
     *            If there are no compilation units to process and/or compile.
     * @throws IOException
     *            If a class or source output location <em>has</em> been set to some file which
     *            does not actually represent an existing directory.
     * @throws IOException
     *            If a class or source output location has <em>not</em> been set and some
     *            {@link IOException} occurs while trying to make a temporary directory to serve as
     *            this output location.
     */
    public CompilationTask build() throws IOException {
        if (isBuilt) {
            String msg = "`CompilationTaskBuilder.build()` can only be called once.";
            throw new IllegalStateException(msg);
        } else {
            CompilationTask retVal = _build();
            _finish();
            return retVal;
        }
    }

    /**
     * <p>The given {@link Processor annotation processor} will be used during the compilation task.
     *
     * <p><em>Warning:</em> Like the {@link Processor} interface, this interface makes no
     * guarantees about the number of rounds in which this task will be called, nor does
     * guarantee the ordering with which the compilation task's processors are called.
     *
     * <p>By default, a compilation task has no processors.
     *
     * @return The receiver instance (i.e. {@code this}).
     */
    public CompilationTaskBuilder addProc(Processor proc) {
        assert proc != null;
        processors.add(proc);
        return this;
    }

    /**
     * <p>A new {@link UniversalProcessor} with the given task will be used during the
     * compilation task.
     *
     * <p>By default, a compilation task has no processors.
     *
     * @return The receiver instance (i.e. {@code this}).
     *
     * @see CompilationTaskBuilder#addProc(Processor)
     * @see UniversalProcessor
     */
    public CompilationTaskBuilder addProc(BiConsumer<ProcessingEnvironment, RoundEnvironment> task) {
        assert task != null;
        addProc(new UniversalProcessor(task));
        return this;
    }

    /**
     * <p>A new {@link CompilationUnitsProcessor} with the given task will be used during the
     * compilation task.
     *
     * <p>By default, a compilation task has no processors.
     *
     * @return The receiver instance (i.e. {@code this}).
     *
     * @see CompilationTaskBuilder#addProc(Processor)
     * @see CompilationUnitTree
     * @see CompilationUnitsProcessor
     */
    public CompilationTaskBuilder addProc(Consumer<CompilationUnitTree> task) {
        assert task != null;
        addProc(new CompilationUnitsProcessor(task));
        return this;
    }

    /**
     * <p>The given {@link StandardJavaFileManager} will be used for the compilation task.
     *
     * <p>By default, a compilation task's file manager is set to the standard file manager
     * (according to the currently-set instance of compiler).
     *
     * <p><em>Warning:</em> In the case that one of file manager configuration methods (e.g.
     * {@link #setClassOutputDir}) is called, those location-settings will mutate the file
     * manager instance passed in here. This mutation will occur at build time. The mutation may
     * affect other contexts if the file manager instance passed in here is being used elsewhere.
     *
     * @return The receiver instance (i.e. {@code this}).
     */
    public CompilationTaskBuilder setFileManager(StandardJavaFileManager fileManager) {
        assert fileManager != null;
        this.fileManager = fileManager;
        return this;
    }

    public CompilationTaskBuilder addClass(String cls) {
        classes.add(cls);
        return this;
    }

    public CompilationTaskBuilder addAllClasses(Iterable<String> cls) {
        cls.forEach(this::addClass);
        return this;
    }

    public CompilationTaskBuilder addOption(String opt) {
        options.add(opt);
        return this;
    }

    public CompilationTaskBuilder addAllOptions(Iterable<String> opts) {
        opts.forEach(this::addOption);
        return null;
    }

    public CompilationTaskBuilder setDiagnosticListener(DiagnosticListener<? super JavaFileObject> diag) {
        diagListener = diag;
        return this;
    }
    
    /**
     * <p>The given compilation unit will be compiled during the compilation task.
     *
     * <p>By default, a compilation task has no compilation units.
     *
     * @return The receiver instance (i.e. {@code this}).
     */
    public CompilationTaskBuilder addCompilationUnit(JavaFileObject unit) {
        assert unit != null;
        compilationUnits.add(unit);
        return this;
    }

    /**
     * <p>All of the given compilation units will be compiled during the compilation task.
     *
     * <p>By default, a compilation task has no compilation units.
     *
     * @return The receiver instance (i.e. {@code this}).
     */
    public CompilationTaskBuilder addAllCompilationUnits(Iterable<JavaFileObject> units) {
        assert units != null;
        units.forEach(this::addCompilationUnit);
        return this;
    }

    /**
     * <p>The compilation task will be processing-only (i.e. "-proc:only" is added as an option).
     *
     * By default, this is set to `false` (i.e. both processing and compilation will occur).
     *
     * @return The receiver instance (i.e. {@code this}).
     */
    public CompilationTaskBuilder addProcOnlyOption() {
        options.add("-proc:only");
        return this;
    }

    private CompilationTask _build() throws IOException {

        // Configure compiler itself.
        JavaCompiler compiler = getSystemJavaCompiler();

        // Configure file manager.
        if (fileManager == null) {
            // TODO: Support passing in other things besides `null`.
            fileManager = compiler.getStandardFileManager(
                    null,
                    null,
                    null
            );
        }

        // Get compilation tasks from class list
        for (String s : classes) {
            JavaFileObject unit = fileManager.getJavaFileForInput(
                    StandardLocation.SOURCE_PATH,
                    s,
                    JavaFileObject.Kind.SOURCE);

            if (unit == null)
                throw new IllegalStateException("No such class " + s);
            
            compilationUnits.add(unit);
        }

        // Configure the compilation task itself.
        if (compilationUnits.isEmpty()) {
            String msg = "CompilationTaskBuilder: No compilation units have been added.";
            throw new IllegalStateException(msg);
        }
        CompilationTask task = compiler.getTask(
                null,             // TODO: Support user-defined writer.
                fileManager,
                diagListener,
                options,
                null,             // TODO: Support user-defined classes.
                compilationUnits
        );
        task.setProcessors(processors);

        return task;
    }

    /**
     * Sets `isBuilt` to true, and sets all fields to `null` for garbage collection.
     */
    private void _finish() {
        isBuilt = true;
        fileManager = null;
        processors = null;
        compilationUnits = null;
        options = null;
    }
}
