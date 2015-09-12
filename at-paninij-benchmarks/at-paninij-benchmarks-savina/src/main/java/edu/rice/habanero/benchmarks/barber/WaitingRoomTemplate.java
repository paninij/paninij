package edu.rice.habanero.benchmarks.barber;

import java.util.LinkedList;
import java.util.Queue;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class WaitingRoomTemplate {

    @Imports Barber barber;

    Queue<Customer> waitingCustomers = new LinkedList<Customer>();
    boolean barberAsleep = true;
    int capacity = SleepingBarberConfig.W;

    @Block
    public boolean enter(Customer c) {
        if (waitingCustomers.size() == capacity) return false;
        waitingCustomers.add(c);

        if (barberAsleep) {
            barberAsleep = false;
            this.next();
        }

        return true;
    }

    public void next() {
        if (waitingCustomers.size() > 0) {
            Customer c = waitingCustomers.poll();
            barber.handle(c);
        } else {
            barberAsleep = true;
        }
    }

    public void done() {
        barber.exit();
    }

}
