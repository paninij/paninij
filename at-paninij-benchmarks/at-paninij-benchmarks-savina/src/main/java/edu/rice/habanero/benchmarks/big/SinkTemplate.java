package edu.rice.habanero.benchmarks.big;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class SinkTemplate {
    @Wired Node[] nodes = new Node[BigConfig.W];
    int numMessages = 0;
    FlagFuture flag = new FlagFuture();

    public FlagFuture start() {
        for (Node n : nodes) n.pong(-1);
        return flag;
    }

    public void finished() {
        numMessages++;
        if (numMessages == BigConfig.W) {
            for (Node n : nodes) n.done();
            flag.resolve();
        }
    }

}
