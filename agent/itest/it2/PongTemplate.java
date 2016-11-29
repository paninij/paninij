package it2;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

import static java.lang.System.out;

@Capsule
public class PongTemplate {

    @Imports Ping ping;

    public void hit(Object o) {
        out.println("Miss!");
    }
}
