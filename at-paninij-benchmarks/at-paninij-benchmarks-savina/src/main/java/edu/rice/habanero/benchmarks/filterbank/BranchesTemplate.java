package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;
import org.paninij.lang.Wired;

@Capsule public class BranchesTemplate {
    @Wired Integrator integrator;
    @Child Bank[] banks = new Bank[FilterBankConfig.NUM_CHANNELS];

    int numChannels = FilterBankConfig.NUM_CHANNELS;
    int numColumns = FilterBankConfig.NUM_COLUMNS;
    double[][] H =  FilterBankConfig.H;
    double[][] F = FilterBankConfig.F;

    public void design(Branches self) {
        for (int i = 0; i < banks.length; i++) {
            banks[i].wire(i, numColumns, FilterBankConfig.H[i], FilterBankConfig.F[i], integrator);
        }
    }

    public void process(double value) {
        for (Bank b : banks) b.process(value);
    }

}
