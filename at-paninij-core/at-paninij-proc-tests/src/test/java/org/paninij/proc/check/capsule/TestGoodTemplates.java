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
package org.paninij.proc.check.capsule;

import static org.paninij.proc.driver.ProcDriver.makeDefaultSettings;

import java.io.IOException;

import org.junit.Test;
import org.paninij.proc.driver.ProcDriver;

public class TestGoodTemplates
{
    private final ProcDriver driver;
    
    public TestGoodTemplates() throws IOException {
        driver = new ProcDriver(makeDefaultSettings());
    }
    
    @Test
    public void testMainGen()
    {
        testGoodTemplates("org.paninij.proc.activepassive", "XTemplate", "YTemplate", "ZTemplate");
    }
    
    @Test
    public void testVarargs()
    {
        testGoodTemplates("org.paninij.proc.check.capsule", "HasVarargsMethodTemplate");
    }

    private void testGoodTemplates(String pkg, String... templates)
    {
        if (templates.length == 0) {
            return;
        }
        if (pkg != null && !pkg.equals("")) {
            for (int idx = 0; idx < templates.length; idx++) {
                templates[idx] = pkg + "." + templates[idx];
            }
        }
        try {
            driver.process(templates);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
