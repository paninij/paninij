package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;
import org.paninij.lang.Wired;

@Capsule public class BankTemplate implements ProcessorTemplate {

    @Wired Integrator integrator;

    @Child Delay delay1;
    @Child FirFilter fir1;
    @Child SampleFilter sample;
    @Child Delay delay2;
    @Child FirFilter fir2;
    @Child TaggedForward tag;

    public void design(Bank self) {
        delay1.wire(fir1);
        fir1.wire(sample);
        sample.wire(delay2);
        delay2.wire(fir2);
        fir2.wire(tag);
        tag.wire(integrator);
    }

    public void initialize(int sourceId, int numColumns, double[] H, double[] F) {
        delay1.initialize(sourceId + ".1", numColumns - 1);
        fir1.initialize(sourceId + ".1", numColumns, H);
        sample.initialize(numColumns);
        delay2.initialize(sourceId + ".2", numColumns -1);
        fir2.initialize(sourceId + ".2", numColumns, F);
        tag.initialize(sourceId);
    }

    @Override
    public void process(double value) {
        delay1.process(value);
    }
}
