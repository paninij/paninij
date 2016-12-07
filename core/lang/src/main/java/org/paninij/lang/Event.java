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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

import org.paninij.runtime.EventMode;

/**
 * A capsule event.
 * 
 * @param <T>
 *            the type of message that can be announced to handlers
 */
public class Event<T> {
    private ConcurrentLinkedQueue<EventConnection<T>> list = new ConcurrentLinkedQueue<>();
    private final EventMode mode;

    public Event(EventMode mode) {
        this.mode = mode;
    }

    /**
     * Registers a reference to a `@Handler` to this event.
     * The registration is by default enabled.
     * 
     * @param handler
     *            method reference to the handler
     * @param type
     *            whether the handler reads or writes
     * @return a subscription to the event
     */
    public EventConnection<T> register(BiConsumer<EventExecution<T>, T> handler, RegisterType type) {
        if (type == RegisterType.WRITE && mode == EventMode.BROADCAST) {
            throw new IllegalArgumentException("Cannot register writer to broadcast event");
        }

        EventConnection<T> conn = new EventConnection<>(handler, type);
        list.add(conn);
        return conn;
    }

    /**
     * Announces this event with the provided message.
     * 
     * @param arg
     *            message to send to handlers
     * @return the announcement's event execution
     */
    public EventExecution<T> announce(T arg) {
        ConcurrentLinkedQueue<EventConnection<T>> announceList = null;
        announceList = new ConcurrentLinkedQueue<>(list);

        EventExecution<T> ex = new EventExecution<>(announceList);
        ex.execute(mode, arg);
        return ex;
    }
}
