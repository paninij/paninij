package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class SampleFilterTemplate implements ProcessorTemplate {

    @Imports Processor next;
    @Imports int sampleRate;

    int samplesReceived = 0;

    @Override
    public void process(double value) {
        if (samplesReceived == 0) {
            next.process(value);
        } else {
            next.process(0);
        }
        samplesReceived = (samplesReceived + 1) % sampleRate;
    }
}
