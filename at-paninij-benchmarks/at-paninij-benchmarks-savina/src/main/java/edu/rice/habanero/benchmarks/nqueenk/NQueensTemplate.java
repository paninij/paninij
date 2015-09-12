package edu.rice.habanero.benchmarks.nqueenk;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class NQueensTemplate {
    @Local Master master;

    public void run() {
        master.start();
    }
}
