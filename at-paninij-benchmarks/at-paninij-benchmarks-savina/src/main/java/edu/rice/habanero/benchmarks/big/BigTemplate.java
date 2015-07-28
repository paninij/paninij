package edu.rice.habanero.benchmarks.big;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class BigTemplate {
    @Child Sink sink;
    @Child Node[] nodes = new Node[BigConfig.W];

    public void design(Big self) {
        for (int i = 0; i < BigConfig.W; i++)
            nodes[i].wire(i, nodes, sink);

        sink.wire(nodes);
    }

    public void run() {
        sink.start();
    }
}
