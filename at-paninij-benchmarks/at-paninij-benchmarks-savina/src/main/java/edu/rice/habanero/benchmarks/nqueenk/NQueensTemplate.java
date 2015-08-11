package edu.rice.habanero.benchmarks.nqueenk;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class NQueensTemplate {
    @Child Master master;

    public void run() {
        master.start();
    }
}
