package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;
import org.paninij.lang.Wired;

@Capsule public class SourceTemplate {
    @Wired Producer producer;
    @Wired Branches branches;

    int maxValue = 1000;
    int current = 0;

    public void start() {
        while (producer.produce()) {
            branches.process(current);
            current = (current + 1) % maxValue;
        }
    }
}
