package org.paninij.ex.xyz;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imported;

@Capsule
public class ZCore {

    @Imported X x;
    @Imported Y y;

    public void run() {
        System.out.println("Hello world");
    }
}
