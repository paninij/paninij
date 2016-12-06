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
 *  Dr. Hridesh Rajan,
 *  Dalton Mills,
 *  David Johnston,
 *  Trey Erenberger
 *  Jackson Maddox
 *******************************************************************************/
package org.paninij.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.paninij.runtime.EventMode;

/**
 * Represents a single announcement of some event.
 *
 * @param <T>
 */
public class EventExecution<T> {
    private List<EventConnection<T>> list;
    private EventMode mode;
    private volatile int marks;
    private volatile int nextIndex;
    private boolean hasExecuted;
    private T arg;
    
    EventExecution(ConcurrentLinkedQueue<EventConnection<T>> list) {
        this.list = new ArrayList<>();
        this.marks = 0;
        this.nextIndex = 0;
        this.hasExecuted = false;
        
        for (EventConnection<T> c : list) {
            if (c.on) {
                this.list.add(c);
            }
        }
    }
    
    /**
     * Lets the announcer capsule know if this event announcement
     * has been completed. i.e. Every subscribed capsule is done
     * with the event data.
     * @return whether this event announcement is completed
     */
    public synchronized boolean isDone() {
        return marks < list.size();
    }
    
    /**
     * Blocks until the event announcement has been completed.
     * i.e. Every subscribed capsule is done with the event data.
     */
    public synchronized void done() {
        while (marks < list.size()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
        
    }
    
    synchronized void execute(EventMode mode, T arg) {
        if (this.hasExecuted) {
            throw new IllegalStateException("Already executed");
        }
        this.hasExecuted = true;
        this.mode = mode;
        this.arg = arg;
        
        if (mode == EventMode.BROADCAST) {
            for (EventConnection<T> c : list) {
                c.handler.accept(this, arg);
            }
        }
        else if (mode == EventMode.CHAIN) {
            doNextInChain();
        }
        else {
            throw new IllegalArgumentException("Unknown event mode");
        }
    }

    public synchronized void panini$markComplete() {
        this.marks++;
        if (list.size() >= marks) {
            this.notifyAll();
        }
        if (mode == EventMode.CHAIN) {
            doNextInChain();
        }
    }
    
    private void doNextInChain() {
        if (marks != nextIndex) {
            return;
        }
        
        boolean hasReading = false;
        for (int i = nextIndex; i < list.size(); i++) {
            EventConnection<T> c = list.get(i);
            if (c.type == RegisterType.READ) {
                c.handler.accept(this, arg);
                hasReading = true;
            } else if (c.type == RegisterType.WRITE) {
                if (hasReading) {
                    nextIndex = i;
                    break;
                } else {
                    nextIndex = i + 1;
                    c.handler.accept(this, arg);
                    break;
                }
            } else {
                throw new RuntimeException("Unknown register type");
            }
        }
    }
}
