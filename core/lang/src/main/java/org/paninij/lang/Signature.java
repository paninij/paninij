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
 * Used to designate a java interface as a core for a signature.
 * <h3>Purpose</h3>
 * The purpose of this annotation is to designate a java interface to act as a core for a signature.
 * 
 * <h3>Details</h3>
 * <p>
 * Signatures are used to allow capsules to have common interfaces which makes it easier build modular systems. A signature acts 
 * in the same manner as a java interface. Capsule cores that implement a signature implement the signature core, but the 
 * artifacts that are generated as a result will use the generated types. (e.g. ConsoleCore implements StreamCore, Console 
 * implements Stream)
 * <h3>Exceptions</h3>
 * <p>
 * An interface annotated with @Signature must have Core at the end of the class name. (e.g. HelloWorldCore)
 * <p>
 * An interface annotated with @Signature may not contain default methods.
 * <h3>Examples</h3>
 * <p>
 * In this example, a basic signature is defined by a java interface. We also define a capsule that implements this signature and as a result 
 * can be referred to using this interface type.
 * <blockquote><pre>
 * &#64;Signature
 * public interface StreamCore {
 *     
 *     public void write(String s);
 * }
 * </pre></blockquote>
 * <blockquote><pre>
 * &#64;Capsule
 * public class ConsoleCore implements StreamCore {
 *     
 *     void write(String s) {
 *         System.out.println(s);
 *     }
 * }
 * </pre></blockquote>
 * <h3>Internal Notes</h3>
 * <p>
 * None
 * <h3>Associated Annotations</h3>
 * <p>
 * This annotation and {@link org.paninij.lang.Capsule @Capsule} are used to set up the class types used in a capsule system.
 **/
public @interface Signature { }
