package edu.rice.habanero.benchmarks.filterbank;

import java.util.Collection;

public class DoubleCollection {
    private Collection<Double> values;

    public DoubleCollection() { }

    public DoubleCollection(Collection<Double> values) {
        this.values = values;
    }

    public Collection<Double> getValues() { return values; }
}
