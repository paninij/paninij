package edu.rice.habanero.benchmarks.pingpong;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class DriverTemplate
{
    @Child Ping ping;
    @Child Pong pong;

    public void design(Driver self) {
        ping.wire(pong);
        pong.wire(ping);
    }

    public void run() {
        FlagFuture f = ping.start();
        while (!f.isDone()) {
            // wait
        }
    }
}
