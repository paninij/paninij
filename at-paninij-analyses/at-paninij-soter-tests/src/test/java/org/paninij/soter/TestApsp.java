package org.paninij.soter;

import org.junit.Test;


public class TestApsp extends AbstractTest
{
    @Override
    protected String[] getCapsules()
    {
        return new String[] {
            "edu.rice.habanero.benchmarks.apsp.Master",
            "edu.rice.habanero.benchmarks.apsp.Worker",
            "edu.rice.habanero.benchmarks.apsp.Apsp"
        };
    }

    @Test
    public void smokeTest() {
        defaultSmokeTest();
    }
}
