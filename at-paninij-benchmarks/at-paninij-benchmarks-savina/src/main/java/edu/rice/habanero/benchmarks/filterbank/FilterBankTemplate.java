package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class FilterBankTemplate {
    @Child Producer producer;
    @Child Source source;
    @Child Branches branches;
    @Child Integrator integrator;
    @Child Combine combine;
    @Child Sink sink;

    public void design(FilterBank self) {
        source.wire(producer, branches);
        branches.wire(integrator);
        integrator.wire(combine);
        combine.wire(sink);
    }

    public void run() {
        FlagFuture wait = sink.getFlag();
        branches.initialize();
        source.start();
        wait.block();
    }
}
