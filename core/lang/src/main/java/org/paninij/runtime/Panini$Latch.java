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
package org.paninij.runtime;

import java.util.concurrent.CountDownLatch;

public class Panini$Latch {
    private CountDownLatch latch;
    private final Object lock = new Object();

    public Panini$Latch() {
        this.latch = new CountDownLatch(0);
    }

    public Panini$Latch(int count) {
        this.latch = new CountDownLatch(count);
    }

    public void await() throws InterruptedException {
        synchronized(lock) {
            while (latch.getCount() != 0) lock.wait();
        }
    }

    public void countDown() throws InterruptedException {
        synchronized(lock) {
            latch.countDown();
            lock.notifyAll();
        }
    }

    public void countUp() throws InterruptedException {
        synchronized(lock) {
            latch = new CountDownLatch((int) latch.getCount() + 1);
            lock.notifyAll();
        }
    }

    public int getCount() {
        synchronized(lock) {
            return (int) latch.getCount();
        }
    }
}
