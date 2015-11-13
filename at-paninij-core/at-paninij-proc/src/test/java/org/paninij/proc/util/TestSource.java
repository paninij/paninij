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

package org.paninij.proc.util;

import org.junit.Test;
import org.paninij.proc.util.Source;

import static org.junit.Assert.assertEquals;

public class TestSource
{
    @Test
    public void formatAlignedBasic()
    {
        final String EXPECTED = "    line1\n"
                              + "    line2\n"
                              + "    line3";

        String fmt = "    ##";
        String actual = Source.formatAligned(fmt, "line1", "line2", "line3");

        assertEquals(EXPECTED, actual);
    }

}
