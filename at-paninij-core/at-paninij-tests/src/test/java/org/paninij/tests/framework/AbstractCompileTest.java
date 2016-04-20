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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class AbstractCompileTest {
    @Rule
    public TestName name = new TestName();
    
    protected IsolatedCompilationTask task;
    private final ArrayList<String> classes;
    
    public AbstractCompileTest(ArrayList<String> classes) {
        this.classes = classes;
    }
    
    @Before
    public void before() throws IOException {
        task = new IsolatedCompilationTask(
                getClass().getName(),
                name.getMethodName());
    }
    
    public static Collection<ArrayList<String>> parameters(String type) throws IOException {
        TestUtil util = new TestUtil(IsolatedCompilationTask.SOURCE_FOLDER);
        util.process();
        return util.getUnits(type);
    }

    public void addClassesAndExecute() throws IOException {
        task.addClasses(classes.toArray(new String[classes.size()]));
        task.execute();
    }
}
