package edu.rice.habanero.benchmarks.philosopher;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class MasterTemplate {
    @Child Arbiter arbiter;

    public void run() {
        arbiter.start();
    }
}
