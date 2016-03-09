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
 *******************************************************************************/
package org.paninij.proc.codegen;

import static org.junit.Assert.assertTrue;
import static org.paninij.proc.driver.ProcDriver.makeDefaultSettings;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.paninij.proc.driver.ProcDriver;

public class TestCodeGenerated
{
    private static final String target = "target/generated-sources/panini";
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
    
    private ProcDriver driver;

    @Before
    public void before() {
        try {
            driver = new ProcDriver(makeDefaultSettings());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean classGenerated(String fullClassName) {
        String[] split = fullClassName.split("\\.");
        StringBuilder path = new StringBuilder(target);
        for (String s : split) {
            path.append("/");
            path.append(s);
        }
        path.append(".java");

        File file = new File(path.toString());
        
        return file.exists();
    }

    public void drive(String fullName) {
        try {
            driver.process(classPackage + "." + fullName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void testGeneratedList(String prefix, String... list) {
        for (String s : list) {
            String fullName = prefix + s;
            assertTrue("Class " + fullName + " does not exist",
                    classGenerated(fullName));
        }
    }
    
    @Test
    public void testDuckGenerated() {
        String runtime = "ducks.org_paninij_proc_codegen_Duckable$Duck$";

        drive("DuckedProcedureTemplate");
        testGeneratedList(runtimePackage + ".", runtime);
        testGeneratedList(classPackage + ".DuckedProcedure", normals);
    }
    
    @Test
    public void testFutureGenerated() {
        String runtime = "futures.org_paninij_proc_codegen_ForFuture$Future$";
        
        drive("FutureProcedureTemplate");
        testGeneratedList(runtimePackage + ".", runtime);
        testGeneratedList(classPackage + ".FutureProcedure", normals);
    }
    
    @Test
    public void testBlockedGenerated() {
        String runtime = "futures.org_paninij_proc_codegen_ForBlock$Future$";

        drive("BlockedProcedureTemplate");
        testGeneratedList(runtimePackage + ".", runtime);
        testGeneratedList(classPackage + ".BlockedProcedure", normals);
    }
}
