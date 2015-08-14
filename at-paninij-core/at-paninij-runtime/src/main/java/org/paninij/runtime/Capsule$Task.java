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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Capsule$Task implements Panini$Capsule
{
    Capsule$Task panini$nextCapsule;
    Panini$TaskPool panini$containingPool;

    protected volatile Object[] panini$queue;

    protected volatile int panini$head, panini$tail, panini$size, panini$links;
    protected final ReentrantLock panini$queueLock;
    protected final Panini$ErrorQueue panini$errors;

    protected boolean panini$terminated;

    public static final int PANINI$CLOSE_LINK = -1;
    public static final int PANINI$TERMINATE = -2;

    protected Capsule$Task() {
        panini$queue = new Object[10];
        panini$head = 0;
        panini$tail = 0;
        panini$size = 0;
        panini$links = 0;
        panini$queueLock = new ReentrantLock();
        panini$errors = new Panini$ErrorQueue();
        panini$terminated = false;
    }

    protected final void panini$extendQueue() {
        assert (panini$tail >= panini$queue.length);

        Object[] newObjects = new Object[panini$queue.length + 10];
        if (panini$tail <= panini$head) {
            System.arraycopy(panini$queue, panini$head, newObjects, 0,
                    panini$queue.length - panini$head);
            System.arraycopy(panini$queue, 0, newObjects, panini$queue.length - panini$head,
                    panini$tail);
        } else {
            System.arraycopy(panini$queue, panini$head, newObjects, 0, panini$tail - panini$head);
        }

        panini$head = 0;
        panini$tail = panini$size;
        panini$queue = newObjects;
    }

    /**
     * Checks to ensure whether this capsule's queue can accommodate numElems number of elements,
     * and if not extends it.
     *
     * @param numElems
     */
    protected final void panini$ensureSpace(int numElems) {
        if (panini$head < panini$tail) {
            if (panini$queue.length + (panini$head - panini$tail) < numElems) {
                if (panini$size != 0) panini$extendQueue();
            }
        } else if (panini$head - panini$tail < numElems) {
            if (panini$size != 0) panini$extendQueue();
        }
    }

    @Override
    public void panini$start() {
        panini$containingPool = Panini$TaskPool.add(this);
    }

    protected final synchronized boolean panini$isEmpty() {
        return panini$size == 0;
    }

    /**
     * Extracts and returns the first message from the capsule's queue.
     *
     * Precondition: it is assumed that `panini$queueLock` is held before calling this method.
     *
     * @return the first available message in the capsule's queue.
     */
    protected final synchronized Panini$Message panini$nextMessage() {
        if (this.panini$size <= 0) return null;
        panini$size--;
        Panini$Message msg = (Panini$Message) panini$queue[panini$head++];
        if (panini$head >= panini$queue.length) panini$head = 0;
        return msg;
    }

    /**
     * Pushes a single object on this capsule's queue.
     *
     * @param o Object to be stored.
     */
    @Override
    public final synchronized void panini$push(Object o)
    {
        panini$ensureSpace(1);
        panini$size = panini$size + 1;
        panini$queue[panini$tail++] = o;

        if (panini$tail >= panini$queue.length) {
            panini$tail = 0;
        }

        if (panini$size == 1) {
            notifyAll();
        }
    }

    protected synchronized void panini$emptyQueue() {
        while (this.panini$size > 0) {
            this.run();
        }
    }


    /**
     * Pushes two objects on this capsule's queue.
     *
     * @param o1 first object to be stored.
     * @param o2 second object to be stored.
     */
    protected final synchronized void panini$push(Object o1, Object o2)
    {
        panini$ensureSpace(2);
        panini$size = panini$size + 2;

        panini$queue[panini$tail++] = o1;
        if (panini$tail >= panini$queue.length) {
            panini$tail = 0;
        }

        panini$queue[panini$tail++] = o2;
        if (panini$tail >= panini$queue.length) {
            panini$tail = 0;
        }

        if (panini$size == 2) {
            notifyAll();
        }
    }


    /**
     * Pushes three objects on this capsule's queue.
     *
     * @param o1 First object to be stored.
     * @param o2 Second object to be stored.
     * @param o3 Third object to be stored.
     */
    protected final synchronized void panini$push(Object o1, Object o2, Object o3)
    {
        panini$ensureSpace(3);
        panini$size = panini$size + 3;

        panini$queue[panini$tail++] = o1;
        if (panini$tail >= panini$queue.length) {
            panini$tail = 0;
        }

        panini$queue[panini$tail++] = o2;
        if (panini$tail >= panini$queue.length) {
            panini$tail = 0;
        }

        panini$queue[panini$tail++] = o3;
        if (panini$tail >= panini$queue.length) {
            panini$tail = 0;
        }

        if (panini$size == 3) {
            notifyAll();
        }
    }


    /**
     * Pushes multiple objects on this capsule's queue.
     *
     * @param items List of objects to be stored.
     */
    protected final synchronized void panini$push(Object... items)
    {
        int numItems = items.length;
        panini$ensureSpace(numItems);
        panini$size = panini$size + numItems;

        for (Object o : items) {
            panini$queue[panini$tail++] = o;
            if (panini$tail >= panini$queue.length) {
                panini$tail = 0;
            }
        }
        if (panini$size == numItems) {
            notifyAll();
        }
    }

    @Override
    public void panini$join() throws java.lang.InterruptedException {
        panini$containingPool.join(); // TODO
    }

    @Override
    public void yield(long millis) {
        // TODO
    }

    @Override
    public void panini$closeLink() {
        panini$push(new SimpleMessage(PANINI$CLOSE_LINK));
    }

    @Override
    public void panini$openLink() {
        panini$links++;
    }

    @Override
    public void exit() {
        this.panini$closeLink();
    }

    protected void panini$onCloseLink() {
        panini$links--;
        if (panini$links == 0 && !panini$terminated) panini$push(new SimpleMessage(PANINI$TERMINATE));
    }

    public final static void panini$init(int size) throws Exception {
        Panini$TaskPool.init(size);
    }

    protected void panini$capsuleInit() {
        panini$checkRequiredFields();
        panini$initChildren();
        panini$initState();
    }

    /**
     * Initialize the capsule-requirements of this capsule.
     *
     * Should be called *before* `panini$initChildren()` or `panini$initState() are called.
     */
    protected void panini$checkRequiredFields() {
        // Do nothing.
    }

    /**
     * Initialize the children of this capsule.
     *
     * Must (in general) be called *before* `panini$initState()`.
     */
    protected void panini$initChildren() {
        // Do nothing.
    }

    /**
     * Initialize the state variables of this capsule.
     */
    protected void panini$initState() {
        // Do nothing.
    }

    /**
     * Send a PANINI$CLOSE_LINK message to all reference capsules
     *
     * Potentially overridden by the implementing capsule
     */
    protected void panini$onTerminate() {
        // Do nothing.
    }

    /**
     * Returns an Object which can be explored to find all the state contained within the capsule.
     */
    public Object panini$getAllState() {
        // Do nothing.
        return null;
    }

    public Throwable panini$pollErrors() {
        return panini$errors.poll();
    }

    public Throwable panini$pollErrors(long timeout, TimeUnit unit) {
        try {
            return panini$errors.poll(timeout, unit);
        } catch (InterruptedException ex) {
            return null;
        }
    }

    /**
     * The method implementation by each concrete capsule with task-based
     * implementation.
     *
     * @return true if the capsule has been terminated.
     */
    abstract protected boolean run();
}
