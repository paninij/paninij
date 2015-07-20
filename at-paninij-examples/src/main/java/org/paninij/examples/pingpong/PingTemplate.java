package org.paninij.examples.pingpong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class PingTemplate {
    @Wired Pong pong;
    @Wired int count;

    public void ping() {
        if (--count > 0) {
            pong.pong(count);
        } else {
            System.out.println("exit");
            pong.exit();
        }
    }

}
