package it3;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule
public class PongTemplate {

    @Imports Ping ping;

    Object obj;
    Foo foo;

    public Object hit(Object o) {
        System.out.println("Pong!");
        ping.hit(expr(o));
        // Something...
        ping.hit(expr(o));
        // Something...
        ping.hit(expr(o));
    }
}
