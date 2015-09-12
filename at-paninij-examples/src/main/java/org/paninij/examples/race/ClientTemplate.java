package org.paninij.examples.race;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class ClientTemplate {
    @Local Server server;
    @Local Relay relay;

    void design(Client self) {
        relay.imports(server);
    }

    void run() {
        // we expect beacon to say Hello first
        relay.sayHello();

        // The message which arrives at the server capsule
        // depends on how long it takes to do this work.
        // less work means the client might say hello first
        // more work means the relay might say hello first
        work(10);

        // we expect client to say Hello last
        server.sayHello("Client");
    }

    private void work(int iter) {
        double y;
        for (int i = 0; i < iter; i++) y = Math.PI;
    }
}
