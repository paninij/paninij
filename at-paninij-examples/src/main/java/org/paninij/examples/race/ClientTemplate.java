package org.paninij.examples.race;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class ClientTemplate
{
    @Local Server server;
    @Local Relay relay;

    void design(Client self) {
        relay.imports(server);
    }

    /**
     * This method illustrates racey behavior.
     * 
     * Since this code triggers `relay` to say hello before the client itself says hello, one
     * might expect the relay to always say Hello first. However, because this is a concurrent
     * system, this is not necessarily the case. The message which actually arrives at the server
     * capsule first depends on how long it takes to do this work.
     *
     * - Less work means that the `Client` is more likely to say hello first.
     * - More work means that the `Relay` is more likely to say hello first.
     * 
     * But in this concurrent system, no matter how large we make the argument to work, there is no
     * absolute guarantee about the order in which the server will receive its messages.
     */
    void run() {
        relay.sayHello();
        work(10);
        server.sayHello("Client");
    }

    @SuppressWarnings("unused")
    private void work(int iter) {
        double y;
        for (int i = 0; i < iter; i++) {
            y = Math.PI;
        }
    }
    
    public static void main(String[] args) {
        CapsuleSystem.start(Client.class.getName(), args);
    }
}
