package it4;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule
public class PongTemplate {

    @Imports Ping ping;

    Object obj;

    public Object hit(Object o) {
        System.out.println("Pong!");
        obj = o;
        return o;
    }
}
