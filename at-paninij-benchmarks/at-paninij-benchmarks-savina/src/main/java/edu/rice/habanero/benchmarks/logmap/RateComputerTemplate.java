package edu.rice.habanero.benchmarks.logmap;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class RateComputerTemplate {

    @Imports int id;
    double rate;

    public void init() {
        this.rate = LogisticMapConfig.startRate + (id * LogisticMapConfig.increment);
    }

    @Block
    public double compute(double term) {
        return LogisticMapConfig.computeNextTerm(term, rate);
    }

}
