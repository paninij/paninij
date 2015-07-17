package edu.rice.habanero.benchmarks.cigsmok;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class SmokerTemplate {
    @Wired Arbiter arbiter;

    public void smoke(int busyWaitPeriod) {
        arbiter.notifySmoking();
        CigaretteSmokerConfig.busyWait(busyWaitPeriod);
    }
}
