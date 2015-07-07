package edu.rice.habanero.benchmarks.barber;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class BarberTemplate {

    @Wired WaitingRoom waitingRoom;
    @Wired CustomerFactory factory;

    Random random = new Random();

    public void handle(Customer c) {
        SleepingBarberConfig.busyWait(random.nextInt(SleepingBarberConfig.AHR) + 10);
        factory.done();
        waitingRoom.next();
    }

}
