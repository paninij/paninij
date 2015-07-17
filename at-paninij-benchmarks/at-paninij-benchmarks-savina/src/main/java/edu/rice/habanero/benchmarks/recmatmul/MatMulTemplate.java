package edu.rice.habanero.benchmarks.recmatmul;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class MatMulTemplate {
    @Child Master master;

    public void run() {
        FlagFuture wait = master.start();
        wait.block();
    }
}
