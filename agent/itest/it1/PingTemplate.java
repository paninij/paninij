package it1;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

import static java.lang.System.out;

@Capsule
public class PingTemplate {

    final int MAX_ITERATIONS = 5;

    @Imports Pong pong;
    int counter = 0;

    public void hit(Object obj) {
        out.println("Ping!");
        if (counter++ < MAX_ITERATIONS) {
            pong.hit(obj);
        } else {
            out.println("Done...");
        }
    }
}
