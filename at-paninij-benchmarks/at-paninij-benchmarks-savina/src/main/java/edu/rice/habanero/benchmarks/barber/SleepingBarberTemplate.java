package edu.rice.habanero.benchmarks.barber;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class SleepingBarberTemplate
{
    @Local CustomerFactory factory;
    @Local Barber barber;
    @Local WaitingRoom room;

    public void design(SleepingBarber self) {
        barber.imports(room, factory);
        room.imports(barber);
        factory.imports(room);
    }

    public void run() {
        factory.start();
    }
}
