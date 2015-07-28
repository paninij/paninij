package edu.rice.habanero.benchmarks.piprecision;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class PiPrecisionTemplate {
    @Child Delegator d;

    public void run() {
        d.start();
    }
}
