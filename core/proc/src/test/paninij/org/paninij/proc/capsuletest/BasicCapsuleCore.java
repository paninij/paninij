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
package org.paninij.proc.capsuletest;

import org.paninij.lang.CapsuleTest;
import org.paninij.lang.Local;
import org.paninij.lang.Test;

/*
@CapsuleTest
class BasicCapsuleCore
{
    @Local Foo foo;
    @Local Bar bar;
    
    void design(BasicCapsule self) {
        foo.imports(bar);
    }
    
    @Test
    void testFooCount()
    {
        Integer count = foo.fooCount();
        assert count.intValue() == 1;
    }
    
    @Test
    void testFooCountAgain()
    {
        Integer count = foo.fooCount();
        assert count.intValue() == 1;
    }

    @Test
    void testBarCount()
    {
        Integer count = bar.barCount();
        assert count.intValue() == 1;
    }

    @Test
    void testImportedBarCount()
    {
        Integer count = foo.importedBarCount();
        assert count.intValue() == 1;
    }
    
    @Test
    void testMultipleCounts()
    {
        final int ITERATIONS = 10;
        final int EXPECTED  = ITERATIONS;

        Integer count = 0;
        for (int idx = 0; idx < ITERATIONS; idx++) {
            count = foo.importedBarCount();
        }
        assert count.intValue() == EXPECTED;
    }
}

*/
