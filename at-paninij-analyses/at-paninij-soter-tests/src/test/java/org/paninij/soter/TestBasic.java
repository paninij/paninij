package org.paninij.soter;

import org.junit.Test;


public class TestBasic extends AbstractTest
{
    @Override
    protected String[] getCapsules()
    {
        return new String[] {
            "org.paninij.soter.ActiveClient",
            "org.paninij.soter.LeakyServer",
            "org.paninij.soter.GossipyClient",
        };
    }

    @Test
    public void smokeTest() {
        defaultSmokeTest();
    }
}
