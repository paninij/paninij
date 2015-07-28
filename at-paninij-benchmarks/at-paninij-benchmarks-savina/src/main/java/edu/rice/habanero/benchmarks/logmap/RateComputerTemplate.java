package edu.rice.habanero.benchmarks.logmap;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class RateComputerTemplate {

    @Wired int id;
    double rate;

    public void init() {
        this.rate = LogisticMapConfig.startRate + (id * LogisticMapConfig.increment);
    }

    public double compute(double term) {
        return LogisticMapConfig.computeNextTerm(term, rate);
    }

}
