package edu.rice.habanero.benchmarks.big;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class NodeTemplate {
    @Imports int id;
    @Imports Node[] nodes = new Node[BigConfig.W];
    @Imports Sink sink;

    int numPings = 0;
    int numMessages = BigConfig.N;
    int expPinger = -1;
    Random random;

    public void init() {
        random = new Random(this.id);
    }

    public void done() {
        for (Node n : nodes) n.exit();
        sink.exit();
    }

    public void ping(int sender) {
        nodes[sender].pong(this.id);
    }

    public void pong(int sender) {
        if (sender != expPinger) {
            System.out.println("ERROR: Expected: " + expPinger + ", but recieved ping from " + sender);
        }

        if (numPings == numMessages) {
            sink.finished();
        } else {
            sendPing();
            numPings++;
        }
    }

    private void sendPing() {
        expPinger = random.nextInt(nodes.length);
        nodes[expPinger].ping(id);
    }
}
