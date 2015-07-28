package edu.rice.habanero.benchmarks.threadring;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class NodeTemplate {
    @Wired Node next;

    public void finish() {
        next.exit();
    }

    public void ping(int count) {
        if (count > 0) {
            count--;
            next.ping(count);
        } else {
            next.finish();
        }
    }

}
