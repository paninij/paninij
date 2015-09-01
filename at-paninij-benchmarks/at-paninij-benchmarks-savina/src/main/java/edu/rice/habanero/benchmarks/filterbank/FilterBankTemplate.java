package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class FilterBankTemplate {
    @Local Producer producer;
    @Local Source source;
    @Local Branches branches;
    @Local Integrator integrator;
    @Local Combine combine;
    @Local Sink sink;

    public void design(FilterBank self) {
        source.imports(producer, branches);
        branches.imports(integrator);
        integrator.imports(combine);
        combine.imports(sink);
    }

    public void run() {
        source.start();
    }
}
