package edu.rice.habanero.benchmarks.pingpong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class PingTemplate {
    private int pings = PingPongConfig.N;
    @Imports Pong pong;

    public void doPing() {
        pings--;
        if (pings > 0) {
            pong.doPong();
        } else {
            pong.exit();
        }
    }
}
