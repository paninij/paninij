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
        Adder next = validator;
        for (int i = 0; i < sortCount; i++) {
            sorters[i].wire(next);
            next = sorters[i];
        }
        source.wire(next);
    }

    public void init() {
        long radix = RadixSortConfig.M /2;
        for (Sort sorter : sorters) {
            sorter.setRadix(radix);
            radix /= 2;
        }
        wait = validator.getFlag();
    }

    public void run() {
        source.start();
        wait.block();
    }
}
