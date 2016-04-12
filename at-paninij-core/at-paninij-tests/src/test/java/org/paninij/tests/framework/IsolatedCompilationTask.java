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
package org.paninij.tests.framework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.paninij.proc.PaniniProcessor;

import me.dwtj.java.compiler.runner.CompilationTaskBuilder;

import javax.tools.JavaFileObject;

public class IsolatedCompilationTask {
    private static final List<File> SOURCE_IN;
    private static final List<File> CLASS_IN;
    private static final String SOURCE_OUT;
    private static final String CLASS_OUT;
    
    public static final File SOURCE_FOLDER;
    
    static {
        SOURCE_FOLDER = new File("src/test/sources/");
                
        SOURCE_IN = new ArrayList<File>();
        SOURCE_IN.add(SOURCE_FOLDER);
        
        CLASS_IN = new ArrayList<File>();
        CLASS_IN.add(new File("../at-paninij-runtime/target/classes/"));
        
        SOURCE_OUT = "target/tests/source/";
        CLASS_OUT = "target/tests/class/";
    }
    
    private final File testSourceOut;
    private final File testClassOut;
    private final StandardJavaFileManager fm;
    private final CompilationTaskBuilder builder;
    private boolean called;
    
    public IsolatedCompilationTask(String unitName, String testName) throws IOException {
        String fullTestName = unitName + "." + testName;
        testSourceOut = new File(SOURCE_OUT + fullTestName);
        testClassOut = new File(CLASS_OUT + fullTestName);
        
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        fm = compiler.getStandardFileManager(null, null, null);
        fm.setLocation(StandardLocation.SOURCE_PATH, SOURCE_IN);
        
        CLASS_IN.add(testClassOut);
        builder = CompilationTaskBuilder.newBuilder();
        builder.setFileManager(fm)
               .setSourceOutputDir(testSourceOut)
               .setClassOutputDir(testClassOut)
               .addAllClassPathDirs(CLASS_IN)
               .addAllSourcePathDirs(SOURCE_IN)
               .addProc(new PaniniProcessor());
    }
    
    public void addClasses(String...classes) throws IOException {
        for (String s : classes) {
            JavaFileObject unit = fm.getJavaFileForInput(
                    StandardLocation.SOURCE_PATH,
                    s,
                    JavaFileObject.Kind.SOURCE);
            
            if (unit == null)
                throw new IllegalStateException("Unit " + s + " is null");
            
            builder.addCompilationUnit(unit);
        }
    }
    
    public void execute() throws IOException {
        if (called)
            throw new IllegalStateException("Cannot execute more than once");
        called = true;
        
        // Clean up from previous tests if needed
        delete(testSourceOut);
        delete(testClassOut);
        
        testSourceOut.mkdirs();
        testClassOut.mkdirs();
        
        CompilationTask task = builder.build();
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
}
