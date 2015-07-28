package edu.rice.habanero.benchmarks.pingpong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class PingPongTemplate {
    @Child Ping ping;
    @Child Pong pong;

    public void design(PingPong self) {
        ping.wire(pong);
        pong.wire(ping);
    }

    public void run() {
        ping.doPing();
    }
}
