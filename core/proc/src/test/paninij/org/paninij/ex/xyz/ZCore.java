package org.paninij.ex.xyz;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imported;

@Capsule
class ZCore {

    @Imported X x;
    @Imported Y y;

    void run() {
        System.out.println("Hello world");
    }
}
