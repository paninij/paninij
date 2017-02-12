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
 * <p>
 * Used to declare local fields on a capsule.
 * <h3>Purpose</h3>
 * The purpose of this annotation is to allow one capsule to hold a reference to another capsule and enable the calling of procedures on that other capsule.
 * 
 * <h3>Details</h3>
 * <p>
 * The @Local annotation is used to set up a relationship between two capsules. When a capsule wishes to utilize procedures on
 * another capsule, it must declare it as a field and annotate it with either @Local or @Imports. This allows the Panini System 
 * to create static connections between capsules and ensure safe concurrency.
 * <p>
 * The difference between @Local and @Imports is that the @Local reference is created and managed by the capsule that includes it as a field. An @Imports 
 * requires that the reference be passed into the capsule at initialization time.
 * <h3>Exceptions</h3>
 * <p>
 * None
 * <h3>Examples</h3>
 * <p>
 * In this example, we have two capsules, the first a passive capsule named Greeter and the second an active capsule called HelloWorld. 
 * The active capsule HelloWorld holds a connection to the passive capsule Greeter in order to call on its procedures. This connection is
 * set up by the @Local annotation on the Greeter field of the HelloWorldCore.  
 * <blockquote><pre>
 * &#64;Capsule
 * public class GreeterCore {
 *     
 *     String message;
 *     
 *     void init() {
 *         message = "Hello!";
 *     }
 *     
 *     &#64;Block
 *     public String getMessage() {
 *         return message;
 *     }
 * }
 * </pre></blockquote>
 * <blockquote><pre>
 * &#64;Capsule
 * public class HelloWorldCore {
 *     &#64;Local Greeter g;
 *     
 *     void run() {
 *         System.out.println(g.getMessage());
 *     }
 * }
 * </pre></blockquote>
 * <h3>Internal Notes</h3>
 * <p>
 * None
 * <h3>Associated Annotations</h3>
 * <p>
 * This annotation and {@link org.paninij.lang.Imports @Imports} are used to define capsule state and connections between capsules in a system.
 *
 */
public @interface Local { /* No annotation arguments. */ }
