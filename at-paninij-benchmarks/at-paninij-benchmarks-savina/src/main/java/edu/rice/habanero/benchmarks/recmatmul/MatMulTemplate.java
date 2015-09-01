package edu.rice.habanero.benchmarks.recmatmul;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class MatMulTemplate {
    @Local Master master;

    public void run() {
        master.start();
    }
}
