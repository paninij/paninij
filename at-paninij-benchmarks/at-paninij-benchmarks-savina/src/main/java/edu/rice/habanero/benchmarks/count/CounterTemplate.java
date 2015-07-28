package edu.rice.habanero.benchmarks.count;

import org.paninij.lang.Capsule;

@Capsule public class CounterTemplate {

    int count = 0;

    public void increment() {
        count++;
    }

    public int result() {
        return count;
    }

}
