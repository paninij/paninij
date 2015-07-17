package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class SampleFilterTemplate implements ProcessorTemplate {

    @Wired Processor next;

    int sampleRate;
    int samplesReceived = 0;

    public void initialize(int sampleRate) {
        this.sampleRate = sampleRate;
    }

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
