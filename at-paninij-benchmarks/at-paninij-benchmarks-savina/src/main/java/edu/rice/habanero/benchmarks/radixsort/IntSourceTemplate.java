package edu.rice.habanero.benchmarks.radixsort;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class IntSourceTemplate {
    Random random = new Random(RadixSortConfig.S);
    @Wired Adder head;

    public void start() {
        for (int i = 0; i < RadixSortConfig.N; i++) {
            long candidate = Math.abs(random.nextLong()) % RadixSortConfig.M;
            head.add(candidate);
        }
    }
}
