package edu.rice.habanero.benchmarks.piprecision;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class WorkerTemplate {
    @Imports Delegator delegator;
    @Imports int id;

    public void work(int term) {
        delegator.resultFinished(PiPrecisionConfig.calculateBbpTerm(PiPrecisionConfig.PRECISION, term), id);
    }
}
