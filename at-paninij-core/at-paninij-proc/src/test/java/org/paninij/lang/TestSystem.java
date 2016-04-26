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
package org.paninij.lang;

import static org.paninij.lang.ExecutionProfile.*;

import java.io.IOException;
import java.lang.String;  // Needed to prevent unintended use of `org.paninij.lang.String`.
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.paninij.runtime.Panini$Capsule$Root;
import org.paninij.test.IsolatedCompilationTask;


public class TestSystem
{
    @Rule
    public TestName name = new TestName();
    
    private static final String[] NO_ARGS = {};
    private static String PACKAGE_PREFIX = "org.paninij.proc.helloworld.";
    private static final String[] CLASSES = {
        "HelloWorldShortTemplate",
        "HelloWorldTemplate",
        "ConsoleTemplate",
        "GreeterTemplate",
        "StreamTemplate"
    };
    
    private static ExecutionProfile[] RUNNABLE_EXECUTION_PROFILES = {
        MONITOR,
        SERIAL,
        TASK,
        THREAD,
    };
    
    private IsolatedCompilationTask task;
    private URLClassLoader newLoader;
    
    @Before
    public void setup() throws IOException {
        task = new IsolatedCompilationTask(
                getClass().getName(), 
                name.getMethodName());
        for (String c : CLASSES) {
            task.addClasses(PACKAGE_PREFIX + c);
        }
        task.execute(false);
    }
    
    @Test
    public void testHelloWorld$Thread() {
        run("HelloWorld", THREAD);
    }
    
    @Test
    public void testHelloWorldShort$Thread() {
        run("HelloWorldShort", THREAD);
    }
    
    @Test
    public void testHelloWorld$All() {
        runWithEachProfile("HelloWorld");
    }
    
    @Test
    public void testHelloWorldShort$All() {
        runWithEachProfile("HelloWorldShort");
    }
    
    private void runWithEachProfile(String capsuleName)
    {
        for (ExecutionProfile profile : RUNNABLE_EXECUTION_PROFILES) {
            run(capsuleName, profile);
        }
    }
    
    private void run(String capsuleName, ExecutionProfile profile) {
        Class<? extends Panini$Capsule$Root> c;
        c = getRootClass(PACKAGE_PREFIX + capsuleName);
        
        CapsuleSystem.start(c, profile, NO_ARGS, newLoader);

        try {
            newLoader.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    private Class<? extends Panini$Capsule$Root> getRootClass(String root) {
        URL[] urls;
        try {
            urls = new URL[]{task.getClassOutput().toURI().toURL()};
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        
        try {
            newLoader = new URLClassLoader(urls, getClass().getClassLoader());
            return (Class<? extends Panini$Capsule$Root>) newLoader.loadClass(root);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } 
    }
}
