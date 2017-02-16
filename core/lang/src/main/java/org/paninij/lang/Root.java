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
 * <p>Used to designate a capsule core as the root capsule.
 * 
 * <h3>Purpose</h3>
 * <p>The purpose of this annotation is to designate a java class to act as a root capsule.
 * 
 * <h3>Details</h3>
 * <p>A root capsule is a capsule from which a capsule  system can be started (often also an
 * active capsule.) A root capsule cannot have any fields annotated with {@link
 * org.paninij.lang.Imported &#64;Imports} (i.e. it cannot have any dependencies). Root capsules are
 * only allowed to send outgoing messages. Therefore it is common for a root capsule to have {@link
 * org.paninij.lang.Local &#64;Local} fields.
 * 
 * <p>There is only one root capsule per capsule system. To start the capsule system, use the
 * {@link org.paninij.lang.CapsuleSystem} class.
 * 
 * <h3>Exceptions</h3>
 * <p>A class annotated with &#64;Root must also be annotated with {@link org.paninij.lang.Capsule
 * &#64;Capsule}.
 *
 * <p>A class annotated with &#64;Root must not contain any fields annotated with {@link
 * org.paninij.lang.Imported &#64;Imports}.
 * 
 * <h3>Examples</h3>
 * <p>In this example, we create a capsule called HelloWorldShort and designate it as the root capsule. A main
 * method is added which starts the root capsule.
 *
 * <h4>HelloWorldShortCore.java</h4>
 * <blockquote><pre>
 * &#64;Root
 * &#64;Capsule
 * public class HelloWorldShortCore {
 * 	public void run() {
 * 		System.out.println("Hello World!");
 *	}
 *
 *	public static void main(String[] args) {
 * 		CapsuleSystem.start(HelloWorldShort.class, args);
 * 	}
 * }
 * </pre></blockquote>
 */
public @interface Root
{
    // Nothing needed here.
}
