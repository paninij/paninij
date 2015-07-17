package edu.rice.habanero.benchmarks.big;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class BigTemplate {
    @Child Sink sink;
    @Child Node[] nodes = new Node[BigConfig.W];

    public void design(Big self) {
        for (Node node : nodes) node.wire(nodes, sink);
        sink.wire(nodes);
    }

    public void run() {
        for (int i = 0; i < BigConfig.W; i++) nodes[i].setId(i);
        FlagFuture wait = sink.start();
        while (!wait.isDone());
    }
}
