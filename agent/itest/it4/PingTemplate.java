package it4;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule
public class PingTemplate {

    @Imports Pong pong;

    public void hit(Object obj) {
        System.out.println("Ping!");
        pong.hit(obj);
    }
}
