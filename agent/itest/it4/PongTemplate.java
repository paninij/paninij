package it4;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule
public class PongTemplate {

    @Imports Ping ping;

    Object obj;

    public void hit(Object o) {
        System.out.println("Pong!");
        obj = o;
        ping.hit(o);  // Creates ownership conflict (if non-null).
    }
}
