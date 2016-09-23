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

import java.util.concurrent.atomic.AtomicInteger;

public final class Panini$TaskPool extends Thread {
    private static volatile boolean initiated = false;
    private static Panini$TaskPool[] pools = new Panini$TaskPool[1];
    private static int poolSize = 1;
    private static int nextPool = 0;
    private static AtomicInteger shutdown = new AtomicInteger(0);
    private static AtomicInteger startup = new AtomicInteger(0);
    private Capsule$Task headNode;

    private Panini$TaskPool() { }

    static final synchronized void init(int size) throws Exception {
        if (initiated) throw new Exception("TaskPool already initialized");
        poolSize = size;
        pools = new Panini$TaskPool[size];
        shutdown.set(0);
        startup.set(0);
        for (int i = 0; i < pools.length; i ++)
            pools[i] = new Panini$TaskPool();

        initiated = true;
    }

    static final synchronized void init() throws Exception {
        init(Panini$System.POOL_SIZE);
    }

    private final synchronized void reset() {
        nextPool = 0;
        poolSize = 1;
        pools = new Panini$TaskPool[1];
        initiated = false;
        shutdown.set(0);
        startup.set(0);
    }

    private final synchronized void shutdown() {
        shutdown.incrementAndGet();
        if (shutdown.get() == startup.get()) {
            reset();
        }
        try {
            Panini$System.threads.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static final synchronized Panini$TaskPool add(Capsule$Task t) {
        if (!initiated) {
            try {
                init();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        // TODO: See load balancing
        int currentPool = nextPool;
        nextPool++;
        if (nextPool >= poolSize) nextPool = 0;
        pools[currentPool]._add(t);
        if (!pools[currentPool].isAlive()) {
            try {
                Panini$System.threads.countUp();
                startup.incrementAndGet();
                pools[currentPool].start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return pools[currentPool];
    }

    private final synchronized void _add(Capsule$Task t) {
        if (headNode == null) {
            headNode = t;
            t.panini$nextCapsule = t;
        } else {
            t.panini$nextCapsule = headNode.panini$nextCapsule;
            headNode.panini$nextCapsule = t;
        }
        t.panini$capsuleInit();
    }

    static final synchronized void remove(Panini$TaskPool pool, Capsule$Task t) {
        pool._remove(t);
    }

    private final synchronized void _remove(Capsule$Task t) {
        Capsule$Task current = headNode;
        Capsule$Task previous = headNode;
        while (current != t) {
            previous = current;
            current = current.panini$nextCapsule;
        }

        if (previous == current) {
            if (current.panini$nextCapsule == current) {
                headNode = null;
                return;
            }
            Capsule$Task tmp = previous;
            while (tmp != previous.panini$nextCapsule)
                previous = previous.panini$nextCapsule;

            headNode = current.panini$nextCapsule;
            previous.panini$nextCapsule = headNode;
        } else {
            previous.panini$nextCapsule = current.panini$nextCapsule;
        }
    }

    @Override
    public void run() {
        // implementation relies on at least one capsule being present
        Capsule$Task current = headNode;
        while (true) {
            if (current.panini$size != 0) {
                if (current.run() == true) remove(this, current);
                if (headNode == null) break;
            }
            synchronized(this) {
                current = current.panini$nextCapsule;
            }
        }
        shutdown();
    }

}
