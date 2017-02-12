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
package org.paninij.lang;
/**
 * <p>Used to dictate capsule-to-thread mappings.
 * 
 * <h3>Mappings</h3>
 * <ol>
 * <li>MOCKUP - Creates capsules with `stub` procedures. Used behind-the-scenes by the annotation 
 * 			processor.</li>
 * <li>THREAD - Each capsule gets it's own JVM thread.</li>
 * <li>TASK - Capsules are assigned to a thread pool in round-robin fashion.</li>
 * <li>MONITOR - Capsules procedures are given basic synchronization.</li>
 * <li>SERIAL - Capsules are sequential (no threads).</li>
 * </ol>
 * 
 * <p>Future works will attempt to automatically assign execution profiles on a per-capsule basis to
 * achieve best results.
 * 
 * <h3>Academic References</h3>
 * <ol>
 * <li>Ganesha Upadhyaya and Hridesh Rajan, Effectively Mapping Linguistic Abstractions for Message-passing 
 * Concurrency to Threads on the Java Virtual Machine, OOPSLA'15
 * </ol>
 */
public enum ExecutionProfile {
    THREAD,
    TASK,
    MONITOR,
    SERIAL,
}
