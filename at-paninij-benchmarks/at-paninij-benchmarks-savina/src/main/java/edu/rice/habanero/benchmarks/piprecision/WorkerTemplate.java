package edu.rice.habanero.benchmarks.piprecision;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class WorkerTemplate {
    @Wired Delegator delegator;
    @Wired int id;

    public void work(int term) {
        delegator.resultFinished(PiPrecisionConfig.calculateBbpTerm(PiPrecisionConfig.PRECISION, term), id);
    }
}
