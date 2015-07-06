package edu.rice.habanero.benchmarks.fjcreate;

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;

@Capsule public class WorkerTemplate {

    @Future
    public void process() {
        ForkJoinConfig.performComputation(37.2);
    }
}
