package edu.rice.habanero.benchmarks.logmap;

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

    public void getTerm() {
        term = computer.compute(term);
    }

    public double getResult() {
        return term;
    }

}
