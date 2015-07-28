package edu.rice.habanero.benchmarks.apsp;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class ApspTemplate {
    @Child Master master;

    public void run() {
        master.start();
    }
}
