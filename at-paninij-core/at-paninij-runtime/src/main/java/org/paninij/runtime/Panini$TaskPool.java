/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, David Johnston, Hridesh Rajan, Trey Erenberger
 */
package org.paninij.runtime;

public final class Panini$TaskPool extends Thread {
    private static boolean initiated = false;
    private static Panini$TaskPool[] pools = new Panini$TaskPool[1];
    private static int poolSize = 1;
    private static int nextPool = 0;
    private Capsule$Task headNode;

    private Panini$TaskPool() { }

    static final synchronized void init(int size) throws Exception {
        if (initiated) throw new Exception("Target already initialized");

        poolSize = size;
        pools = new Panini$TaskPool[size];
        for (int i = 0; i < pools.length; i ++)
            pools[i] = new Panini$TaskPool();

        initiated = true;
    }

    private final synchronized void reset() {
        nextPool = 0;
        poolSize = 1;
        pools = new Panini$TaskPool[1];
        initiated = false;
    }

    static final synchronized Panini$TaskPool add(Capsule$Task t) {
        // TODO: See load balancing
        int currentPool = nextPool;
        nextPool++;
        if (nextPool >= poolSize) nextPool = 0;
        pools[currentPool]._add(t);
        if (!pools[currentPool].isAlive()) pools[currentPool].start();
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
        t.panini$capsuleInit(); // TODO ???
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
        this.reset();
    }

}
