package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;

@Capsule public class ProducerTemplate {

    int numMessagesSent = 0;
    int numSimulations = FilterBankConfig.NUM_SIMULATIONS;

    public boolean produce() {
        if (numMessagesSent >= numSimulations) {
            return false;
        } else {
            numMessagesSent++;
            return true;
        }
    }

}
