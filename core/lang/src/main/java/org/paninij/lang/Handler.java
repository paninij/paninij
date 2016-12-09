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
 * Used to designate a procedure as an event handler.
 * </p>
 * 
 * <h3>Purpose</h3>
 * <p>
 * The @Handler annotation specifies that a procedure can be registered to events,
 * and are not to be used for any other purpose.
 * </p>
 * 
 * <h3>Details</h3>
 * <p>
 * The handler must have exactly one parameter of some type that it can receive from
 * the event. It will be generated as such to be compatible with event registration
 * and announcement. When an event announcement is received, the handler will be invoked.
 * </p>
 */
@Documented
public @interface Handler { }
