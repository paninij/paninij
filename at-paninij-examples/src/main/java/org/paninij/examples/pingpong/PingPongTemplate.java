package org.paninij.examples.pingpong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class PingPongTemplate {
    @Child Ping ping;
    @Child Pong pong;

    public void design(PingPong self) {
        ping.wire(pong, 100000);
        pong.wire(ping);
    }

    public void run() {
        ping.ping();
    }

}
