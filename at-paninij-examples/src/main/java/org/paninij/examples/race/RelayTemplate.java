package org.paninij.examples.race;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class RelayTemplate {
    @Wired Server server;

    void sayHello() {
        server.sayHello("Relay");
    }

}
