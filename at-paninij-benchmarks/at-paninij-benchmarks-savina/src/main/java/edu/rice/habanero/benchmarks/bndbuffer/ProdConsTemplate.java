package edu.rice.habanero.benchmarks.bndbuffer;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class ProdConsTemplate {
    @Local Manager manager;

    public void run() {
        manager.start();
    }
}
