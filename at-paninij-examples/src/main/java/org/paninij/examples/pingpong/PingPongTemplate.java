package org.paninij.examples.pingpong;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;

@Capsule public class PingPongTemplate
{
    @Local Ping ping;
    @Local Pong pong;

    public void design(PingPong self) {
        ping.imports(pong, 100000);
        pong.imports(ping);
    }

    public void run() {
        ping.ping();
    }

    public static void main(String[] args) {
        CapsuleSystem.start(PingPong.class.getName(), args);
    }
}
