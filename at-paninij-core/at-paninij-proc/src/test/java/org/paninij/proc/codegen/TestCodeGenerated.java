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
 * 	Jackson Maddox
 *******************************************************************************/
package org.paninij.proc.codegen;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.paninij.test.IsolatedCompilationTask;

public class TestCodeGenerated
{
    private static final String classPackage = "org.paninij.proc.codegen";
    private static final String runtimePackage = "org.paninij.runtime";
    private static final String normals[] = {
            "",
            "$Mockup",
            "$Monitor",
            "$Serial",
            "$Task",
            "$Thread",
        };
    
    private IsolatedCompilationTask task;

    @Rule
    public TestName name = new TestName();
    
    @Before
    public void before() throws IOException {
        task = new IsolatedCompilationTask(
                getClass().getName(), name.getMethodName());
    }

    private boolean classGenerated(String fullClassName) {
        String pathName = fullClassName.replaceAll("\\.", "/");
        File file = new File(task.getSourceOutput().getPath() +
                "/" + pathName + ".java");
        return file.exists();
    }

    private void drive(String... classNames) throws IOException {
        for (int i = 0; i < classNames.length; i++) {
            classNames[i] = classPackage + "." + classNames[i];
        }
        task.addClasses(classNames);
        task.execute(false);
    }

    private void testGeneratedList(String prefix, String... list) {
        for (String s : list) {
            String fullName = prefix + s;
            assertTrue("Class " + fullName + " does not exist",
                    classGenerated(fullName));
        }
    }
    
    @Test
    public void testDuckGenerated() throws IOException {
        String runtime = "ducks.org_paninij_proc_codegen_ProcReturn$Duck$";

        drive("DuckedProcedureTemplate", "ProcReturn");
        testGeneratedList(runtimePackage + ".", runtime);
        testGeneratedList(classPackage + ".DuckedProcedure", normals);
    }
    
    @Test
    public void testFutureGenerated() throws IOException {
        String runtime = "futures.org_paninij_proc_codegen_ProcReturn$Future$";
        
        drive("FutureProcedureTemplate", "ProcReturn");
        testGeneratedList(runtimePackage + ".", runtime);
        testGeneratedList(classPackage + ".FutureProcedure", normals);
    }
    
    @Test
    public void testBlockedGenerated() throws IOException {
        String runtime = "futures.org_paninij_proc_codegen_ProcReturn$Future$";

        drive("BlockedProcedureTemplate", "ProcReturn");
        testGeneratedList(runtimePackage + ".", runtime);
        testGeneratedList(classPackage + ".BlockedProcedure", normals);
    }
}
