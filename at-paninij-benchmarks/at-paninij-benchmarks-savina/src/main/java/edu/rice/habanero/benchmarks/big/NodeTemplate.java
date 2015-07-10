package edu.rice.habanero.benchmarks.big;

import java.util.Random;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class NodeTemplate {
    @Wired Node[] nodes = new Node[BigConfig.W];
    @Wired Sink sink;

    int id;
    int numPings = 0;
    int numMessages = BigConfig.N;
    int expPinger = -1;
    Random random;

    @Block
    public void setId(int id) {
        this.id = id;
        random = new Random(id);
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
