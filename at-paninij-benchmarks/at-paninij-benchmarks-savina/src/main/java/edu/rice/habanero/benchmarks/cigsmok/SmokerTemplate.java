package edu.rice.habanero.benchmarks.cigsmok;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class SmokerTemplate {
    @Imports Arbiter arbiter;

    public void smoke(int busyWaitPeriod) {
        arbiter.notifySmoking();
        CigaretteSmokerConfig.busyWait(busyWaitPeriod);
    }
}
