package edu.rice.habanero.benchmarks.concdict;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class DictTemplate {
    @Child Master master;

    public void run() {
        master.start();
    }

}
