package edu.rice.habanero.benchmarks.philosopher;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class MasterTemplate {
    @Child Arbiter arbiter;

    public void run() {
        FlagFuture wait = arbiter.start();
        while (!wait.isDone());
    }
}
