package edu.rice.habanero.benchmarks.fjcreate;

import org.paninij.lang.Capsule;
import org.paninij.lang.Root;

@Root
@Capsule
public class ForkJoinTemplate {
    public void run() {
        ForkJoinConfig.performComputation(37.2);
    }
}
