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
package org.paninij.benchmarks.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * The ThreadTimer keeps track of stop and start times for arbitrary ID's with respect to threads.
 */
public class ThreadTimer  {

    static Map<Integer, Long> timers = new HashMap<Integer, Long>();

    /**
     * Retrieve the current time in nanoseconds that the current thread
     * has executed while in User Mode.
     */
    public static long current() {
        ThreadMXBean manager = ManagementFactory.getThreadMXBean();
        if (manager.isCurrentThreadCpuTimeSupported()) {
            return manager.getCurrentThreadUserTime();
        }
        return 0;
    }

    /**
     * Start a timer with given ID
     */
    public static void start(int id) {
        timers.put(id, current());
    }

    /**
     * Get the current time of given ID
     */
    public static long get(int id) {
        long t1 = timers.get(id);
        long t2 = current();;
        return t2 - t1;
    }

    /**
     * Stop a timer with given ID
     */
    public static void stop(int id) {
        long start = timers.get(id);
        long diff = get(id);
        timers.remove(id);
        log(id, start, diff);
    }

    private static void log(int id, long start, long diff) {
        System.out.println(id + " : [" + start + "] + " + diff);
    }

}
