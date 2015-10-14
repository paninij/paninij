package org.paninij.soter;

import org.junit.Test;


public class TestMisc extends AbstractTest
{
    @Override
    protected String[] getCapsules()
    {
        return new String[] {
            "org.paninij.soter.NoTransfers",
            "org.paninij.soter.TwoPathsToTransfer",
        };
    }

    @Test
    public void smokeTest() throws Exception
    {
        defaultSmokeTest();
    }
}
