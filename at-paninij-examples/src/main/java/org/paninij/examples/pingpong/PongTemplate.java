package org.paninij.examples.pingpong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class PongTemplate {
    @Wired Ping ping;

    public void pong(int n) {
        ping.ping();
        if (n % 1000 == 0) System.out.println("count = " + n);
    }
}
