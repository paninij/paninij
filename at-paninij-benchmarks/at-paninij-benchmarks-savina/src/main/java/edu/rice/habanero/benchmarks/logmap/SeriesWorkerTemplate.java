package edu.rice.habanero.benchmarks.logmap;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class SeriesWorkerTemplate {
    @Wired Master master;
    @Wired RateComputer computer;

    int id;
    double startTerm;
    double curTerm;

    public void initialize(int id, double startTerm) {
        this.id = id;
        this.startTerm = startTerm;
        this.curTerm = startTerm;
    }

    public Result getTerm() {
        return computer.compute(curTerm, id);
    }

}
