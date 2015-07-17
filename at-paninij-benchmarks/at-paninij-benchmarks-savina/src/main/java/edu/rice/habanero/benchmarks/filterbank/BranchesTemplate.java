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
        for (Bank b : banks) b.wire(integrator);
    }

    public void initialize() {
        FilterBankConfig.parseArgs(new String[0]);

//        double[][] H = new double[numChannels][numColumns];
//        double[][] F = new double[numChannels][numColumns];
//
//        for (int j = 0; j < numChannels; j++) {
//            for (int i = 0; i < numColumns; i++) {
//                H[j][i] = (1.0 * i * numColumns) + (1.0 * j * numChannels) + j + i + j + 1;
//                F[j][i] = (1.0 * i * j) + (1.0 * j * j) + j + i;
//            }
//        }


        for (int i = 0; i < banks.length; i++) {
            banks[i].initialize(i, numColumns, FilterBankConfig.H[i], FilterBankConfig.F[i]);
        }
    }

    public void process(double value) {
        for (Bank b : banks) b.process(value);
    }

}
