package edu.rice.habanero.benchmarks.piprecision;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class WorkerTemplate {
    @Wired Delegator d;

    public Result work(int term, int indx) {
        Result r = new Result(PiPrecisionConfig.calculateBbpTerm(PiPrecisionConfig.PRECISION, term), indx);
        d.resultFinished(indx);
        return r;
    }
}
