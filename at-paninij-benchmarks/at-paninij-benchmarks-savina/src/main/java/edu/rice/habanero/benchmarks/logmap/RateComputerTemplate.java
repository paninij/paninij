package edu.rice.habanero.benchmarks.logmap;

import org.paninij.lang.Capsule;

@Capsule public class RateComputerTemplate {

    double rate;

    public void initialize(int id) {
        this.rate = LogisticMapConfig.startRate + (id * LogisticMapConfig.increment);
    }

    public Result compute(double term) {
        return new Result(LogisticMapConfig.computeNextTerm(term, rate));
    }

}
