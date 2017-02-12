package org.paninij.ex.xyz;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule
public class ZTemplate {

    @Imports X x;
    @Imports Y y;

    public void run() {
        System.out.println("Hello world");
    }
}
