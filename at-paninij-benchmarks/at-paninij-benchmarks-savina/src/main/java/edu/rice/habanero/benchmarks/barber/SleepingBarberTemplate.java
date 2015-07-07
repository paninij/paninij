package edu.rice.habanero.benchmarks.barber;

import java.util.concurrent.ExecutionException;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class SleepingBarberTemplate {
    @Child CustomerFactory factory;
    @Child Barber barber;
    @Child WaitingRoom room;

    public void design(SleepingBarber self) {
        barber.wire(room, factory);
        room.wire(barber);
        factory.wire(room);
    }

    public void run() {
        FlagFuture wait = factory.start();
        try {
            wait.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
