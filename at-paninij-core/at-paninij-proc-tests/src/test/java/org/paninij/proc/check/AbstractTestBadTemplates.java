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
package org.paninij.proc.check;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.paninij.proc.driver.ProcDriver.makeDefaultSettings;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import org.paninij.proc.driver.ProcDriver;

public abstract class AbstractTestBadTemplates
{
    private final ProcDriver driver;
    
    public AbstractTestBadTemplates()
    {
        try {
            driver = new ProcDriver(makeDefaultSettings());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Before
    public void setUp()
    {
        expected.expect(RuntimeException.class);
        expected.expectCause(instanceOf(getExpectedCause()));
        // TODO: Figure out if we can specify the expected error source (i.e. the check from which
        // the exception was originally thrown).
    }
    
    protected void testBadTemplate(String badTemplate)
    {
    	testBadTemplate(badTemplate, getBadTemplatePackage());
    }
    
    protected void testBadTemplate(String badTemplate, String pack)
    {
        try {
        	String full = pack.equals("") ? badTemplate : pack + "." + badTemplate;
            driver.process(full);
        } catch (RuntimeException ex) {
            // TODO: Log error messages from checks better.
            //System.err.println(ex.getMessage());  // For logging check messages
            throw ex;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected abstract String getBadTemplatePackage();
    
    protected abstract Class<?> getExpectedCause();
}
