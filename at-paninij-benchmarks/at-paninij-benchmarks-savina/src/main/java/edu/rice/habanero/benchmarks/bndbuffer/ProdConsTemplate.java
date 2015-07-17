package edu.rice.habanero.benchmarks.bndbuffer;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class ProdConsTemplate {
    @Child Manager manager;

    public void run() {
        FlagFuture wait = manager.start();
        wait.block();
    }
}
