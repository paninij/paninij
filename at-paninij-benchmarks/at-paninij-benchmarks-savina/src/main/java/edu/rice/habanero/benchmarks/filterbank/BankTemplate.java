package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;
import org.paninij.lang.Wired;

@Capsule public class BankTemplate implements ProcessorTemplate {

    @Wired int sourceId;
    @Wired int numColumns;
    @Wired double[] H;
    @Wired double[] F;
    @Wired Integrator integrator;

    @Child Delay delay1;
    @Child FirFilter fir1;
    @Child SampleFilter sample;
    @Child Delay delay2;
    @Child FirFilter fir2;
    @Child TaggedForward tag;

    public void design(Bank self) {
        delay1.wire(fir1, sourceId + ".1", numColumns - 1);
        fir1.wire(sample, sourceId + ".1", numColumns, H);
        sample.wire(delay2, numColumns);
        delay2.wire(fir2, sourceId + ".2", numColumns -1);
        fir2.wire(tag, sourceId + ".2", numColumns, F);
        tag.wire(integrator, sourceId);
    }

    @Override
    public void process(double value) {
        delay1.process(value);
    }
}
