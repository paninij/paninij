package edu.rice.habanero.benchmarks.logmap;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Imports;

@Capsule public class SeriesWorkerTemplate {
    @Local RateComputer computer;

    @Imports int id;
    @Imports double term;

    public void design(SeriesWorker self) {
        computer.imports(id);
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
