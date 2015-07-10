package edu.rice.habanero.benchmarks.threadring;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class NodeTemplate {
    @Wired Node next;

    public void finish() {
        next.exit();
    }

    public void ping(int count, FlagFuture flag) {
        if (count > 0) {
            count--;
            next.ping(count, flag);
        } else {
            flag.resolve();
            next.finish();
        }
    }

}