package it6;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

import static java.lang.System.out;

@Root @Capsule
class ClientTemplate {

    @Local Server server;

    void run() {
        out.println(server.getObject());
    }
}
