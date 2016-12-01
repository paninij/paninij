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

import java.util.function.BiConsumer;

/** 
 * A subscription of a handler to an event.
 * 
 * @param <T>
 */
public class PaniniConnection<T> {
    BiConsumer<PaniniEventExecution<T>, T> handler;
    RegisterType type;
    volatile boolean on;

    PaniniConnection(BiConsumer<PaniniEventExecution<T>, T> handler, RegisterType type) {
        this.handler = handler;
        this.type = type;
        this.on = true;
    }

    /** 
     * Enables this handler to receive new announcements.
     */
    public void on() {
        on = true;
    }

    /**
     * Stops the handler from receiving new announcements.
     * This does not affect announcements that were initiated
     * but have not reached this handler before this is called.
     */
    public void off() {
        on = false;
    }
}
