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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Capsule$Thread implements Panini$Capsule, Runnable
{
    protected Thread panini$thread;
    protected volatile Object[] panini$queue;
    protected volatile int panini$head, panini$tail, panini$size, panini$links;

    protected final ReentrantLock panini$queueLock;

    protected final Panini$ErrorQueue panini$errors;

    protected boolean panini$terminated;

    public static final int PANINI$CLOSE_LINK = -1;
    public static final int PANINI$TERMINATE = -2;


    protected Capsule$Thread()
    {
        panini$queue = new Object[10];
        panini$head = 0;
        panini$tail = 0;
        panini$size = 0;
        panini$links = 0;
        panini$queueLock = new ReentrantLock();
        panini$terminated = false;
        panini$errors = new Panini$ErrorQueue();
    }


    protected final void panini$extendQueue()
    {
        assert (panini$tail >= panini$queue.length);

        Object[] newObjects = new Object[panini$queue.length + 10];
        if (panini$tail <= panini$head)
        {
            System.arraycopy(panini$queue, panini$head, newObjects, 0,
                    panini$queue.length - panini$head);
            System.arraycopy(panini$queue, 0, newObjects, panini$queue.length - panini$head,
                    panini$tail);
        }
        else
        {
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
    protected final void panini$ensureSpace(int numElems)
    {
        if (panini$head < panini$tail)
        {
            if (panini$queue.length + (panini$head - panini$tail) < numElems) {
                if (panini$size != 0) {
                    panini$extendQueue();
                }
            }
        }
        else if (panini$head - panini$tail < numElems)
        {
            if (panini$size != 0) {
                panini$extendQueue();
            }
        }
    }

    /**
     * Extracts and returns the first duck from the capsule's queue. This method blocks if there
     * are no ducks in the queue.
     *
     * Precondition: it is assumed that `panini$queueLock` is held before calling this method.
     *
     * @return the first available duck in the capsule's queue.
     */
    protected final synchronized Panini$Message panini$nextMessage()
    {
        if (this.panini$size <= 0)
            panini$blockCapsule();
        panini$size--;
        Panini$Message msg = (Panini$Message) panini$queue[panini$head++];
        if (panini$head >= panini$queue.length)
            panini$head = 0;
        return msg;
    }


    private final void panini$blockCapsule()
    {
        nomessages: while (this.panini$size <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                continue nomessages;
            }
        }
    }

    protected final boolean panini$isEmpty() {
        return panini$size == 0;
    }

    /**
     * Causes the current capsule to sleep (temporarily cease execution) for the specified number
     * of milliseconds, subject to the precision and accuracy of system timers and schedulers. The
     * capsule does not lose ownership of any monitors.
     *
     * @param millis The length of time to sleep in milliseconds
     * @throws IllegalArgumentException If the value of millis is negative
     *
     */
    @Override
    public void yield(long millis)
    {
        if (millis < 0) {
            throw new IllegalArgumentException();
        }

        try {
            Thread.sleep(millis);
            // TODO: this may also be a good place to introduce interleaving.
        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO: What should be the semantics here?
        }
    }

    @Override
    public void exit() {
        // TODO ???
        this.panini$closeLink();
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

    /**
     * Initialize the capsule-requirements of this capsule.
     *
     * Should be called *before* `panini$initLocals()` or `panini$initState() are called.
     */
    protected void panini$checkRequiredFields() {
        // Do nothing.
    }

    /**
     * Initialize the locals of this capsule.
     *
     * Must (in general) be called *before* `panini$initState()`.
     */
    protected void panini$initLocals() {
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

    @Override
    public void panini$start()
    {
        try {
            Panini$System.threads.countUp();
            panini$thread = new Thread(this);
            panini$thread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void panini$join()
    {
        while (true)
        {
            try {
                panini$thread.join();
            } catch (InterruptedException e) { /* Do nothing: try again to join indefinitely. */ }
        }
    }

    @Override
    public void panini$openLink() {
        panini$links++;
    }

    @Override
    public void panini$closeLink() {
        panini$push(new SimpleMessage(PANINI$CLOSE_LINK));
    }

    protected void panini$onCloseLink() {
        panini$links--;
        if (panini$links == 0 && !panini$terminated) panini$push(new SimpleMessage(PANINI$TERMINATE));
    }

    public Throwable panini$pollErrors() {
        return panini$errors.poll();
    }

    public Throwable panini$pollErrors(long timeout, TimeUnit unit)
    {
        try {
            return panini$errors.poll(timeout, unit);
        } catch (InterruptedException ex) {
            return null;
        }
    }
}