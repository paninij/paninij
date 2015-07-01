package edu.rice.habanero.benchmarks.piprecision;

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;
import org.paninij.lang.Wired;

@Capsule public class WorkerTemplate {
    @Wired Delegator d;

    public Result work(int scale, int term, int indx) {
        Result r = new Result(PiPrecisionConfig.calculateBbpTerm(scale, term), indx);
        d.resultFinished(indx);
        return r;
    }
}
