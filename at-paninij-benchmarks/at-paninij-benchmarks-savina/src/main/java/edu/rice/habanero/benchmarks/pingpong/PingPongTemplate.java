package edu.rice.habanero.benchmarks.pingpong;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class PingPongTemplate
{
    @Child Ping ping;
    @Child Pong pong;

    public void design(PingPong self) {
        ping.wire(pong);
        pong.wire(ping);
    }

    public void run() {
        FlagFuture f = ping.start();
        try {
            f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
