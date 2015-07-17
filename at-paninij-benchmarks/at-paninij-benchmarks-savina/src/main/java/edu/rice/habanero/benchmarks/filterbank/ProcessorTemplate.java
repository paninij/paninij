package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Signature;

@Signature public interface ProcessorTemplate {
    public void process(double value);
}
