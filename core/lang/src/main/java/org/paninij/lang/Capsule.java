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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>
 * Used to designate a java class as a core for a capsule.
 * <h3>Purpose</h3>
 * The purpose of this annotation is to designate a java class to act as a core for a capsule.
 * 
 * <h3>Details</h3>
 * <p>
 * When a class is annotated with &#64;Capsule, it will get processed and capsule artifacts will be generated based on the procedures 
 * and state of the java class. The name of the capsule produced will be the class name of the annotated class with Core removed. 
 * (e.g. HelloWorldCore produces HelloWorld capsule) A capsule is subject to a number of restrictions based on the Panini model.
 * <h3>Exceptions</h3>
 * <p>
 * A class annotated with &#64;Capsule must have Core at the end of the class name. (e.g. HelloWorldCore)
 * <h3>Examples</h3>
 * <p>
 * In this example, a basic capsule is described by a java class. This will generate capsule artifacts which can be run as a capsule system.
 * <blockquote><pre>
 * &#64;Capsule
 * public class HelloWorldShortCore {
 *     
 *     void run() {
 *         System.out.println("Hello World!");
 *     }
 * }
 * </pre></blockquote>
 * <h3>Internal Notes</h3>
 * <p>
 * None
 * <h3>Associated Annotations</h3>
 * <p>
 * This annotation and {@link org.paninij.lang.Signature &#64;Signature} are used to set up the class types used in a capsule system.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Capsule {}
