package edu.rice.habanero.benchmarks.logmap;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class SeriesWorkerTemplate {
    @Child RateComputer computer;

    int id;
    double startTerm;
    Result result;

    public void initialize(int id, double startTerm) {
        this.id = id;
        this.startTerm = startTerm;
        this.result = new Result(startTerm);
        computer.initialize(id);
    }

    public void getTerm() {
        result = computer.compute(result.getTerm());
    }

    public double getResult() {
        return result.getTerm();
    }

}
