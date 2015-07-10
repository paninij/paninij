package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class DelayTemplate implements ProcessorTemplate {

    @Wired Processor next;

    String sourceId;
    int delayLength;

    int placeHolder = 0;
    double[] state;

    public void initialize(String sourceId, int delayLength) {
        this.sourceId = sourceId;
        this.delayLength = delayLength;
        this.state = new double[delayLength];
    }

    @Override
    public void process(double value) {
        next.process(state[placeHolder]);
        state[placeHolder] = value;
        placeHolder = (placeHolder + 1) % delayLength;
    }
}
