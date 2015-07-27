package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class FirFilterTemplate implements ProcessorTemplate {

    @Wired Processor next;
    @Wired String sourceId;
    @Wired int peekLength;
    @Wired double[] coefficients;

    double[] data;
    int dataIndex = 0;
    boolean dataFull = false;

    public void init() {
        this.data = new double[peekLength];
    }

    @Override
    public void process(double value) {

        data[dataIndex] = value;
        dataIndex++;

        if (dataIndex == peekLength) {
            dataFull = true;
            dataIndex = 0;
        }

        if (dataFull) {
            double sum = 0.0;
            for (int i = 0; i < peekLength; i++) {
                sum += (data[i] * coefficients[peekLength - i - 1]);
            }
            next.process(sum);
        }
    }
}
