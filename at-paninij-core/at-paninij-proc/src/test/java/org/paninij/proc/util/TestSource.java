
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
