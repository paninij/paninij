package edu.rice.habanero.benchmarks.barber;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class CustomerFactoryTemplate {

    @Imports WaitingRoom waitingRoom;

    Random random = new Random();
    int numHaircutsSoFar = 0;
    int haircuts = SleepingBarberConfig.N;
    AtomicLong idGenerator = new AtomicLong(0);

    private void sendCustomerToRoom() {
        Customer c = new Customer(idGenerator.incrementAndGet());
        sendCustomerToRoom(c);
    }

    private void sendCustomerToRoom(Customer c) {
        boolean entered = waitingRoom.enter(c);
        if (!entered) returned(c);
    }

    public void start() {
        for (int i = 0; i < haircuts; i++) {
            sendCustomerToRoom();
            SleepingBarberConfig.busyWait(random.nextInt(SleepingBarberConfig.APR) + 10);
        }
    }

    public void returned(Customer c) {
        idGenerator.incrementAndGet();
        sendCustomerToRoom(c);
    }

    public void done() {
        numHaircutsSoFar++;
        if (numHaircutsSoFar == haircuts) {
            waitingRoom.done();
            waitingRoom.exit();
            System.out.println("Total attempts: " + idGenerator.get());
        }
    }

}
