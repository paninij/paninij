package edu.rice.habanero.benchmarks.radixsort;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class RadixSortTemplate {
    int sortCount = (int) (Math.log(RadixSortConfig.M) / Math.log(2));

    @Child Validation validator;
    @Child IntSource source;
    @Child Sort[] sorters = new Sort[sortCount];

    FlagFuture wait;

    public void design(RadixSort self) {
        long radix = RadixSortConfig.M /2;
        Adder next = validator;

        for (int i = 0; i < sortCount; i++) {
            sorters[i].wire(next, radix);
            next = sorters[i];
            radix /= 2;
        }

        source.wire(next);
    }

    public void run() {
        source.start();
    }
}
