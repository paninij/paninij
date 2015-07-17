package edu.rice.habanero.benchmarks.pingpong;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule
public class PingTemplate
{
    private int pings = PingPongConfig.N;
    @Wired Pong pong;
    FlagFuture finished = new FlagFuture();

    public FlagFuture start() {
        this.doPing();
        return finished;
    }

    public void doPing() {
        pings--;
        if (pings > 0) {
            pong.doPong();
        } else {
            pong.exit();
            finished.resolve();
        }
    }
}
