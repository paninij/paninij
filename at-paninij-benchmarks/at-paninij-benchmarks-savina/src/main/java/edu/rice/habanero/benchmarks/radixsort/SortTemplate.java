package edu.rice.habanero.benchmarks.radixsort;
import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

import edu.rice.habanero.benchmarks.radixsort.RadixSortConfig;


@Capsule public class SortTemplate implements AdderTemplate {
    @Wired Adder next;
    @Wired Long radix;

    int valuesSoFar = 0;

    long[] orderingArray = new long[RadixSortConfig.N];
    int j = 0;

    @Override
    public void add(long value) {
        valuesSoFar++;

        if ((value & radix) == 0) {
            next.add(value);
        } else {
            orderingArray[j] = value;
            j++;
        }

        if (valuesSoFar == RadixSortConfig.N) {
            for (int i = 0; i < j; i++) {
                next.add(orderingArray[i]);
            }
        }

    }
}
