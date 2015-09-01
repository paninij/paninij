package edu.rice.habanero.benchmarks.big;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class BigTemplate {
    @Local Sink sink;
    @Local Node[] nodes = new Node[BigConfig.W];

    public void design(Big self) {
        for (int i = 0; i < BigConfig.W; i++)
            nodes[i].imports(i, nodes, sink);

        sink.imports(nodes);
    }

    public void run() {
        sink.start();
    }
}
