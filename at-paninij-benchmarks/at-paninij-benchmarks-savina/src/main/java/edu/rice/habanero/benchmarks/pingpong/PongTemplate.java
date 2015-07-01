package edu.rice.habanero.benchmarks.pingpong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule
public class PongTemplate
{
    @Wired Ping ping;
    private int pongs = 0;

    public void doPong() {
        pongs++;
        ping.doPing();
    }
}
