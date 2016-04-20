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
package org.paninij.proc.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.StandardJavaFileManager;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;

import org.paninij.proc.PaniniProcessor;

import me.dwtj.java.compiler.runner.CompilationTaskBuilder;
import me.dwtj.java.compiler.runner.StandardJavaFileManagerBuilder;

public class IsolatedCompilationTask {
    public static final File SOURCE_FOLDER;

    private static final File RUNTIME_CLASSES_FOLDER;
    private static final String SOURCE_OUT;
    private static final String CLASS_OUT;
    private static final String TARGET;
    
    static {
        SOURCE_FOLDER = new File("src/test/sources/");
        RUNTIME_CLASSES_FOLDER = new File("../at-paninij-runtime/target/classes/");
        SOURCE_OUT = "/sources/";
        CLASS_OUT = "/classes/";
        TARGET = "target/tests/";
    }
    
    private final File sourceOutput;
    private final File classOutput;
    private final StandardJavaFileManagerBuilder fmBuilder;
    private final CompilationTaskBuilder ctBuilder;
    private boolean called;
    
    public IsolatedCompilationTask(String unitName, String testName) throws IOException {
        String fullTestName = unitName + "." + testName;
        sourceOutput = new File(TARGET + fullTestName + SOURCE_OUT);
        classOutput = new File(TARGET + fullTestName + CLASS_OUT);
        
        fmBuilder = StandardJavaFileManagerBuilder.newBuilder();
        ctBuilder = CompilationTaskBuilder.newBuilder();
    }
    
    public void addClasses(String...classes) throws IOException {
        ctBuilder.addAllClasses(Arrays.asList(classes));
    }
    
    public void exceptOnCompileError() {
        ctBuilder.setDiagnosticListener(new DiagExcept());
    }
    
    public void execute() throws IOException {
        if (called)
            throw new IllegalStateException("Cannot execute more than once");
        called = true;
        
        // Clean up from previous tests if needed
        delete(sourceOutput);
        delete(classOutput);
        
        sourceOutput.mkdirs();
        classOutput.mkdirs();
        
        // Build file manager and task
        fmBuilder.setSourceOutputDir(sourceOutput)
                 .setClassOutputDir(classOutput)
                 .addSourcePathDir(SOURCE_FOLDER)
                 .addClassPathDir(classOutput)
                 .addClassPathDir(RUNTIME_CLASSES_FOLDER);
        
        StandardJavaFileManager fileManager = fmBuilder.build();
        
        ctBuilder.setFileManager(fileManager)
                  .addProc(new PaniniProcessor())
                  .addOption("-Apanini.exceptOnFailedChecks");
        
        CompilationTask task = ctBuilder.build();
        
        // Execute compiler
        task.call();
    }
    
    private void delete(File file) {
        if (!file.exists())
            return;
        
        if (file.isDirectory()) {
          for (File f : file.listFiles()) {
              delete(f);
          }
        }
        file.delete();
    }
    
    private static class DiagExcept implements DiagnosticListener<JavaFileObject> {
        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            System.err.println(diagnostic.toString());
            throw new RuntimeException(
                    "Compile error: " +
                    diagnostic.getMessage(Locale.getDefault()));
        }
    }
}
