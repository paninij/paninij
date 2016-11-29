package it1;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule
public class PongTemplate {

    @Imports Ping ping;

    public void hit(Object o) {
        System.out.println("Pong!");
        ping.hit(o);
    }
}
