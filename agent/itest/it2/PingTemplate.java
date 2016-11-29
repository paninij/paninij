package it2;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

import static java.lang.System.out;

@Capsule
public class PingTemplate {

    @Imports Pong pong;

    Object obj;

    public void hit(Object o) {
        out.println("Ping!");
        pong.hit(o);  // Ownership conflict!
        obj = o;      // Use-after-move.
    }
}
