package edu.rice.habanero.benchmarks.logmap;

import org.paninij.lang.Capsule;

@Capsule public class RateComputerTemplate {

    double rate;

    public void initialize(double rate) {
        this.rate = rate;
    }

    public Result compute(double term, int id) {
        return new Result(LogisticMapConfig.computeNextTerm(term, rate), id);
    }

}
