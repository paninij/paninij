package it2;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root @Capsule
public class ServerTemplate {
    @Local Ping ping;
    @Local Pong pong;

    void design(Server self) {
        ping.imports(pong);
        pong.imports(ping);
    }

    public void run() {
        System.out.println("Serving...");
        ping.hit(new Object());
    }
}
