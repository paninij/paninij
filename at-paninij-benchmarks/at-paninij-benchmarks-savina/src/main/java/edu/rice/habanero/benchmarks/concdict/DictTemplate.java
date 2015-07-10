package edu.rice.habanero.benchmarks.concdict;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class DictTemplate {
    @Child Master master;

    public void run() {
        FlagFuture wait = master.start();
        while (!wait.isDone());
    }

}
