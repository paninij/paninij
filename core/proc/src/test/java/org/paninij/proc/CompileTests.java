package org.paninij.proc;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.dwtj.java.compiler.utils.CompilationTaskBuilder;
import org.paninij.proc.check.CheckException;

import javax.tools.JavaCompiler.CompilationTask;

import static java.io.File.separatorChar;
import static me.dwtj.java.compiler.utils.CompilationTaskBuilder.compileProperties;
import static me.dwtj.java.compiler.utils.CompilationTaskBuilder.newBuilder;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.rules.ExpectedException.none;

/**
 * Runs a suite of compilation tasks over various @PaniniJ sources to test various aspects of the
 * annotation processor. This is primary suite of integration tests for the annotation processor.
 * Each compilation in this test suite is constructed from a {@code config.properties} file. The
 * parameters of the compilation are set by inspecting this file and by adding-in some standard
 * Panini-testing-specific configuration (e.g. putting {@code proc} itself on the classpath, setting
 * the classpath to be
 *
 * <p>This JUnit test searches the Panini test sources directory for files which describe a
 * compilation test to be performed. These files are identified by the filename suffix {@code
 * compile.properties}. (Note that {@code compile.properties} itself qualifies.)</p>
 *
 * <p>Each file is interpreted as a compilation task using {@link
 * CompilationTaskBuilder#newBuilder(File)}. This means that the given file is interpreted as an
 * Apache Commons Configuration properties file. This file format is an extension of the standard
 * Java {@link java.util.Properties} file format. One notable and useful difference that we
 * depend upon is that a list of values can be encoded in a properties file. This is done by
 * having multiple key-value pairs with the same key. For example, one can specify multiple source
 * files to be compiled in the following way:</p>
 *
 * <pre><code>
 * src = org.paninij.Foo
 * src = org.paninij.Bar
 * src = org.paninij.Baz
 * </code></pre>
 *
 * <p>Another useful feature of this file format, variable interpolation, allows us to write this
 * same list using the following syntax:</p>
 *
 * <pre><code>
 * pkg = org.paninij
 * src = ${pkg}.Foo
 * src = ${pkg}.Bar
 * src = ${pkg}.Baz
 * </code></pre>
 *
 * <p>These {@code compile.properties} files are expected to include a number of known properties,
 * and it is these known properties which are interpreted in the construction of the integration
 * test compilation task. The {@code src} property is one example. Known properties for a Panini
 * compilation properties include all of the properties known by
 * {@link CompilationTaskBuilder#newBuilder(Configuration)} (e.g. {@code src}, {@code class_path}),
 * but in this context a {@code compile.properties} file may include one more known property:
 * {@code exception}.
 *
 * The {@code exception} property's value indicates the type of exception which is expected to be
 * thrown during the compilation task. If the {@code exception} property does not appear in a
 * {@code compile.properties} file, then that indicates that no exceptions are expected to be thrown
 * from the compilation task.
 *
 * Currently, the only valid value for the {@code exception} property is {@code CheckException},
 * which indicates that the user expects some {@link CheckException} to be thrown during the
 * compilation task because some input violates one or more of the Panini-specific checks in
 * {@link org.paninij.proc.check}. Other known properties may be added in the future.
 *
 * <p>A {@code compile.properties} file does not fully/explicitly describe the test compilation task
 * to be run. The configuration of a testing compilation task is implicitly modified to include some
 * default behavior shared across all of the test compilations. For each compilation task, this
 * implicit configuration includes:</p>
 *
 * <ul>
 *   <li>The addition of {@link #SOURCES_DIR} to the {@code source_path}.</li>
 *   <li>The addition of the @PaniniJ annotation processor itself to the {@code class_path}.</li>
 *   <li>The selection of appropriate {@code source_output} & {@code class_output} directories.</li>
 * </ul>
 *
 * This last item is important. This testing system is designed to make all of the test compilations
 * isolated from one another. One way in which we achieve this is by making sure that the source
 * and class outputs of one compilation are not mixed with those of another compilation. This is
 * done by giving each compilation its own file system locations for these source and class outputs.
 * See {@link #sourceOutputDir()} and {@link #classOutputDir()} for more.
 *
 * @author dwtj
 */

// TODO: Generalize/extend the use of `exception`

@RunWith(Parameterized.class)
public class CompileTests {

    // Note: Because of how Gradle invokes this JUnit test, file paths are relative to the root
    // directory of the `:core:proc` subproject. Hopefully this behavior is consistent.
    // TODO: Relative paths seem brittle. It might be worthwhile to make this more robust somehow.
    private static final File SOURCES_DIR = new File("src/test/paninij");
    private static final String OUTPUTS_PREFIX = "build/proc-tests";
    private static final String SOURCE_OUTPUT_DIR_SUFFIX = "gen-src";
    private static final String CLASS_OUTPUT_DIR_SUFFIX = "classes";
    private static final File PANINI_RUNTIME_CLASSES_DIR = new File("../lang/build/classes/main");
    private static final File PANINI_PROC_CLASSES_DIR = new File("build/classes/main");
    private static final String COMPILE_PROPERTIES = "compile.properties";

    private static final FileFilter IS_COMPILE_PROPERTIES_FILE =
            f -> (f.isFile() && f.getName().endsWith(COMPILE_PROPERTIES));

    @Parameter(0) public String compileTestName;
    @Parameter(1) public File compilePropertiesFile;

    public CompilationTask compilationTask;

    @Parameters(name="{0}")
    public static Collection<Object[]> compilePropertiesFiles() {
        List<Object[]> files = new ArrayList<>();
        compilePropertiesFiles(SOURCES_DIR, files);
        return files;
    }

    /**
     * Recursively searches a file hierarchy for compile properties files, adding all found files
     * to the given list.
     */
    private static void compilePropertiesFiles(File searchDir, List<Object[]> files) {
        assert searchDir.exists() && searchDir.isDirectory();
        for (File f : denullify(searchDir.listFiles(IS_COMPILE_PROPERTIES_FILE))) {
            files.add(new Object[] { name(f), f });
        }
        for (File f : denullify(searchDir.listFiles(File::isDirectory))) {
            compilePropertiesFiles(f, files);
        }
    }

    @Rule
    public ExpectedException exception = none();

    private boolean expectedToHaveCompileErrors;

    @Before
    public void init() throws IOException, ClassNotFoundException {
        PropertiesConfiguration config = compileProperties(compilePropertiesFile);
        CompilationTaskBuilder taskBuilder = newBuilder(config);

        // Add the implicit/standard configuration to this test compilation task.
        taskBuilder.addProc(new RoundZeroProcessor())
                   .addProc(new RoundOneProcessor());
        taskBuilder.getFileManagerConfig().addToSourcePath(SOURCES_DIR)
                                          .addToClassPath(PANINI_PROC_CLASSES_DIR)
                                          .addToClassPath(PANINI_RUNTIME_CLASSES_DIR)
                                          .setSourceOutputDir(sourceOutputDir())
                                          .setClassOutputDir(classOutputDir());

        // TODO: Clarify the distinction between compile errors and exception.

        // Note: One might imagine that we could simplify this system by just looking for compile
        // errors and not using exceptions from `proc` at all. However, exception throwing/catching
        // makes it easy to have precise determinations using the Java type system.

        // TODO: Look into how to compilation diagnostics can be inspected to see if they'd work.

        // For now, assume that a compilation task--if it terminates without an exception--should
        // always finish without any compile errors.
        expectedToHaveCompileErrors = false;

        // Make sure that the output directories exist.
        sourceOutputDir().mkdirs();
        classOutputDir().mkdirs();

        // Check whether we should expect an exception.
        String exceptionStr = config.getString("exception", "");
        if (exceptionStr.equals("")) {
            exception = none();
        } else if (exceptionStr.equals("CheckException")) {
            // Expect the JUnit test to catch a `RuntimeException` containing a nested
            // `CheckException`. (The compiler itself wraps any exception thrown from the
            // annotation processor in a `RuntimeException`.)
            exception.expect(isA(RuntimeException.class));
            exception.expectCause(isA(CheckException.class));
        } else {
            String msg = "`exception` property set to an unexpected value: " + exceptionStr;
            throw new RuntimeException(msg);
        }

        // Build the compilation task itself.
        compilationTask = taskBuilder.build();
    }

    @Test
    public void compile() {
        boolean actuallyHasCompileErrors = !compilationTask.call();
        assertEquals(expectedToHaveCompileErrors, actuallyHasCompileErrors);
    }

    private File sourceOutputDir() {
        return new File(OUTPUTS_PREFIX + "/" + compileTestName + "/" + SOURCE_OUTPUT_DIR_SUFFIX);
    }

    private File classOutputDir() {
        return new File(OUTPUTS_PREFIX + "/" + compileTestName + "/" + CLASS_OUTPUT_DIR_SUFFIX);
    }

    private static String name(File compilePropertiesFile) {
        Path path = SOURCES_DIR.toPath().relativize(compilePropertiesFile.toPath());
        String name = path.toString();
        name = name.replace(separatorChar, '.');
        final String SUFFIX = "." + COMPILE_PROPERTIES;
        assert name.endsWith(SUFFIX);
        name = name.substring(0, name.length() - SUFFIX.length());  // Drop SUFFIX.
        assert name.length() > 0;
        return name;
    }

    private static File[] denullify(File[] arr) {
        return (arr == null) ? new File[0] : arr;
    }
}
