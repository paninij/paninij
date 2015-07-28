package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;

@Capsule public class SinkTemplate {
    int printRate = FilterBankConfig.SINK_PRINT_RATE;
    int count = 0;
    int total = 0;

    public void process(double value) {
        if (FilterBankConfig.debug && (count == 0)) System.out.println("SinkActor: result = " + value);
        count = (count + 1) % printRate;
        total++;
    }

}
