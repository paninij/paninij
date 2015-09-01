package org.paninij.examples.race;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class RelayTemplate {
    @Imports Server server;

    void sayHello() {
        server.sayHello("Relay");
    }

}
