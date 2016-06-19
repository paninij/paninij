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

public class Panini$System
{

    public static int POOL_SIZE = 4;
    public static Panini$Latch threads = new Panini$Latch();

    /**
     * System-global variable meant to hold thread-local references to a capsule instance. This
     * makes it possible for a capsule--regardless of the environment or context in which it is
     * executing--can always access itself (i.e. the capsule variable's `this`).
     *
     * (The initial motivation for this was to enable a capsule to get a reference to itself while
     * it was running the procedure wrapper on another capsule.)
     */
    public static final ThreadLocal<Capsule$Thread> self = new ThreadLocal<Capsule$Thread>();
}
