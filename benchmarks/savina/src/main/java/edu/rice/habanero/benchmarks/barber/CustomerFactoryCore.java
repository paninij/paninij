/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package edu.rice.habanero.benchmarks.barber;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imported;

@Capsule public class CustomerFactoryCore {

    @Imported
    WaitingRoom waitingRoom;

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
