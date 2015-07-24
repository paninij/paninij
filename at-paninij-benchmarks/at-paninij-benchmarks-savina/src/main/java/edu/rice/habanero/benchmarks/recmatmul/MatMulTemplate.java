package edu.rice.habanero.benchmarks.recmatmul;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class MatMulTemplate {
    @Child Master master;

    public void run() {
        master.start();
    }
}
