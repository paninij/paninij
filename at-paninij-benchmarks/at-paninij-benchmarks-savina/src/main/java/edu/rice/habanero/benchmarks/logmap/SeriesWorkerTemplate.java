package edu.rice.habanero.benchmarks.logmap;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;
import org.paninij.lang.Wired;

@Capsule public class SeriesWorkerTemplate {
    @Child RateComputer computer;

    @Wired int id;
    @Wired double term;

    public void design(SeriesWorker self) {
        computer.wire(id);
    }

    @Block
    public void getTerm() {
        term = computer.compute(term);
    }

    @Block
    public double getResult() {
        return term;
    }

}
