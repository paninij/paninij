package edu.rice.habanero.benchmarks.apsp;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class ApspTemplate {
    @Local Master master;

    public void run() {
        master.start();
    }
}
