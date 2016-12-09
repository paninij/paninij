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
 * Used to declare `broadcast` announcement behavior of an event. 
 * </p>
 * 
 * <h3>Purpose</h3>
 * <p>
 * This annotation specifies the announcement behavior of the event it annotates.
 * When an event is annotated by `@Broadcast`, it can accept only read handlers.
 * </p>
 * 
 * <h3>Details</h3>
 * <p>
 * The broadcast announcement behavior means that all subscribers must be read only.
 * If a write handler attempts to register to a broadcast event, the registration
 * will throw an exception. Since subsribers are all readers, announcement messages 
 * are sent to every subscriber simultaneously. 
 * </p>
 * 
 */
@Documented
public @interface Broadcast { }
