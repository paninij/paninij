package edu.rice.habanero.benchmarks.barber;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class BarberTemplate {

    @Imports WaitingRoom waitingRoom;
    @Imports CustomerFactory factory;

    Random random = new Random();

    public void handle(Customer c) {
        SleepingBarberConfig.busyWait(random.nextInt(SleepingBarberConfig.AHR) + 10);
        factory.done();
        waitingRoom.next();
    }

}
