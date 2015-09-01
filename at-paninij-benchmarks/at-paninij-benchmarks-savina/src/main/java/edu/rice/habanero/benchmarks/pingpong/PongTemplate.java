package edu.rice.habanero.benchmarks.pingpong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class PongTemplate {
    @Imports Ping ping;
    private int pongs = 0;

    public void doPong() {
        pongs++;
        ping.doPing();
    }
}
