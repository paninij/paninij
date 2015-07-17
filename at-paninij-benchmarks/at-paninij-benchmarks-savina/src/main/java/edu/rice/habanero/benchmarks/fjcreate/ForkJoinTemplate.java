package edu.rice.habanero.benchmarks.fjcreate;

import org.paninij.lang.Capsule;

@Capsule public class ForkJoinTemplate {
    public void run() {
        ForkJoinConfig.performComputation(37.2);
    }
}
