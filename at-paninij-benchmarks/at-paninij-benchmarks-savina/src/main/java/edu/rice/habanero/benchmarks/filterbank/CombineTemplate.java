package edu.rice.habanero.benchmarks.filterbank;

import java.util.Collection;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class CombineTemplate {

    @Wired Sink sink;

    public void process(DoubleCollection collection) {
        double sum = 0;
        Collection<Double> values = collection.getValues();
        for (Double d : values) sum += d;
        sink.process(sum);
    }

}
