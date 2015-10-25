package org.paninij.soter;

import org.junit.Test;


public class TestTriviallySafe extends AbstractTest
{
    @Override
    protected String[] getCapsules()
    {
        return new String[] {
            "org.paninij.soter.TriviallySafeInvokeTransfers",
            "org.paninij.soter.TriviallySafeReturnTransfers",
        };
    }

    @Test
    public void smokeTest() {
        defaultSmokeTest();
    }
}
