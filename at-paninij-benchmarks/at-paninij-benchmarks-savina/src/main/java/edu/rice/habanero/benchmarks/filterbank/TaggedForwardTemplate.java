package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class TaggedForwardTemplate implements ProcessorTemplate {

    @Imports Integrator integrator;
    @Imports int sourceId;

    @Override
    public void process(double value) {
        integrator.process(sourceId, value);
    }
}
