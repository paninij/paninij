package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class DelayTemplate implements ProcessorTemplate {

    @Imports Processor next;
    @Imports String sourceId;
    @Imports int delayLength;

    int placeHolder = 0;
    double[] state;

    public void init() {
        this.state = new double[delayLength];
    }

    @Override
    public void process(double value) {
        next.process(state[placeHolder]);
        state[placeHolder] = value;
        placeHolder = (placeHolder + 1) % delayLength;
    }
}
