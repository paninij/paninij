package org.paninij.examples.pingpong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class PongTemplate {
    @Imports Ping ping;

    public void pong(int n) {
        ping.ping();
        if (n % 1000 == 0) System.out.println("count = " + n);
    }
}
