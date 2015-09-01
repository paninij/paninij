package edu.rice.habanero.benchmarks.pingpong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class PingPongTemplate {
    @Local Ping ping;
    @Local Pong pong;

    public void design(PingPong self) {
        ping.imports(pong);
        pong.imports(ping);
    }

    public void run() {
        ping.doPing();
    }
}
