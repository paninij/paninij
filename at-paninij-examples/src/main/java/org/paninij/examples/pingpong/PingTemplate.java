package org.paninij.examples.pingpong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class PingTemplate {
    @Imports Pong pong;
    @Imports int count;

    public void ping() {
        if (--count > 0) {
            pong.pong(count);
        } else {
            System.out.println("exit");
            pong.exit();
        }
    }

}
