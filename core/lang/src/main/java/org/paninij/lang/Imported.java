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
 * Used to declare imported fields on a capsule.
 * <h3>Purpose</h3>
 * The purpose of this annotation is to allow one capsule to hold a reference to another capsule and enable the calling of procedures on that other capsule.
 * 
 * <h3>Details</h3>
 * <p>
 * The @Imported annotation is used to create a connection between capsules. It is used to signify that the reference to the capsule must be passed in 
 * at initialization. The field with the @Imported annotation specifies a connection to another capsule that this capsule does not initialize. The parent of the
 * capsule core with the @Imported field must supply the reference via a design method call. 
 * <p>
 * The difference between @Local and @Imported is that the @Local reference is created and managed by the capsule that includes it as a field. An @Imported 
 * requires that the reference be passed into the capsule at initialization time.
 * <h3>Exceptions</h3>
 * <p>
 * None
 * <h3>Examples</h3>
 * <p>
 * In this example, we have three capsules, the first is a passive capsule named Console, the second is a passive capsule named Greeter and the third is an active capsule called HelloWorld. 
 * The active capsule HelloWorld holds a connection to the passive capsules Greeter and Console in order to call on its procedures. This connection is
 * set up by the @Local annotation on the Greeter and Console field of the HelloWorldCore. 
 * <p>
 * The capsule Greeter has a field of type Console that is annotated with the @Imported. This means
 * that the Greeter wants to call procedures of a Console capsule, but does not want to create the instance of the capsule. By using @Imported, it specifies that the parent capsule of the Greeter
 * must supply the reference to the Console capsule. This is shown in the HelloWorldCore in the design method where g.imports is called and the reference to the Console capsule is passed as
 * a parameter.  
 * <h4>ConsoleCore.java</h4>
 * <blockquote><pre>
 * &#64;Capsule
 * public class ConsoleCore {
 *     
 *     &#64;Block
 *     public void write(String s) {
 *         System.out.println(s);
 *     }
 * }
 * </pre></blockquote>
 * <h4>GreeterCore.java</h4>
 * <blockquote><pre>
 * &#64;Capsule
 * public class GreeterCore {
 *     
 *     &#64;Imports Console c;
 *     String message;
 *     
 *     void init() {
 *         message = "Hello";
 *     }
 *     
 *     &#64;Block
 *     public void sendMessage() {
 *         c.write(message);
 *     }
 * }
 * </pre></blockquote>
 * <h4>HelloWorldCore.java</h4>
 * <blockquote><pre>
 * &#64;Capsule
 * public class HelloWorldCore {
 *     &#64;Local Greeter g;
 *     &#64;Local Console c;
 *     
 *     void design(HelloWorld self) {
 *         g.imports(c);
 *     }
 *     
 *     void run() {
 *         g.sendMessage();
 *     }
 * }
 * </pre></blockquote>
 * 
 * <h3>Associated Annotations</h3>
 * <p>
 * This annotation and {@link org.paninij.lang.Local @Local} are used to define capsule state and connections between capsules in a system.
 *
 */
public @interface Imported { /* No annotation arguments. */ }
