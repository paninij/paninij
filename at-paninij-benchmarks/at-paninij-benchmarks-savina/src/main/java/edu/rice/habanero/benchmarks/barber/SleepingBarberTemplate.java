package edu.rice.habanero.benchmarks.barber;

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
        factory.start();
    }
}
