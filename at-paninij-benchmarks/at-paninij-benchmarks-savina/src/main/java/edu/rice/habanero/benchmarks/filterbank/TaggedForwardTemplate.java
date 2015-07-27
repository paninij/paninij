package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class TaggedForwardTemplate implements ProcessorTemplate {

    @Wired Integrator integrator;
    @Wired int sourceId;

    @Override
    public void process(double value) {
        integrator.process(sourceId, value);
    }
}
