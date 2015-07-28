package edu.rice.habanero.benchmarks.cigsmok;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class CigaretteSmokerTemplate {
    @Child Arbiter arbiter;

    public void run() {
        arbiter.start();
    }
}
