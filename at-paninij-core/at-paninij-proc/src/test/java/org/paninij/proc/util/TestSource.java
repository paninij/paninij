/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): David Johnston
 */
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
