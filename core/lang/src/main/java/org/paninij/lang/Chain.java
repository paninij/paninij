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
 *  Jackson Maddox
 *******************************************************************************/

package org.paninij.lang;

import java.lang.annotation.Documented;

/**
 * <p>
 * Used to declare `chained` announcement behavior of an event. 
 * </p>
 * 
 * <h3>Purpose</h3>
 * <p>
 * This annotation specifies the announcement behavior of the event it annotates.
 * When an event is annotated by `@Chain`, it can accept both read and write handlers.
 * </p>
 * 
 * <h3>Details</h3>
 * <p>
 * The chained announcement behavior means that subscribers are examined in the order
 * that they were registered. Groups of read-only subscribers are notified all at once
 * while writing subscribers are notified individually. Once a group of read-only
 * subscribers or a writing subscriber is notified, no other subscribers are notified
 * until the current subscribers' handlers have returned.
 * </p>
 * 
 * <h3>Example</h3>
 * <p>
 * Suppose W1 and W2 are writing subscribers and R1, R2, R3 are read-only subscribers
 * that were registered in the order of W1, R1, R2, W2, R3. Then the event's announcement
 * proceeds as follows: W1 -> R1, R2 -> W2 -> R3 where R1 and R2 are notified at the
 * same time and -> denotes the RHS being notified only after the LHS completes.
 * </p>
 * 
 */
@Documented
public @interface Chain { }
