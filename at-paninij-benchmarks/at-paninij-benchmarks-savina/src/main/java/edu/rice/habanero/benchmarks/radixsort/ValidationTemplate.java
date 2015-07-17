package edu.rice.habanero.benchmarks.radixsort;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;

@Capsule public class ValidationTemplate implements AdderTemplate {

    int valuesSoFar = 0;
    double sumSoFar = 0.0;
    long prevValue = 0L;

    long errVal = -1;
    int errIndx = -1;

    FlagFuture flag = new FlagFuture();

    public FlagFuture getFlag() {
        return flag;
    }

    @Override
    public void add(long value) {
        valuesSoFar++;
        if (value < prevValue && errVal < 0) {
            errVal = value;
            errIndx = valuesSoFar - 1;
        }

        prevValue = value;
        sumSoFar += prevValue;

        if (valuesSoFar == RadixSortConfig.N) {
            if (errVal >= 0) {
                System.out.println("ERROR: Value out of place: " + errVal + " at index " + errIndx);
            } else {
                System.out.println("Elements sum: " + sumSoFar);
            }
            flag.resolve();
        }

    }

}
