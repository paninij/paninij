package org.paninij.examples.race;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class RelayTemplate {
    @Imports Server server;

    public void sayHello() {
        server.sayHello("Relay");
    }

}
