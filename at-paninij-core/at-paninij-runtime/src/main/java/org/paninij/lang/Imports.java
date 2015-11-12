/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, David Johnston, Kristin Clemens, Trey Erenberger
 */
package org.paninij.lang;
/**
 * <p>
 * Used to declare imported fields on a capsule.
 * <h3>Purpose</h3>
 * The purpose of this annotation is to allow one capsule to hold a reference to another capsule and enable the calling of procedures on that other capsule.
 * 
 * <h3>Details</h3>
 * <p>
 * The @Imports annotation is used to create a connection between capsules. It is used to signify that the reference to the capsule must be passed in 
 * at initialization. The field with the @Imports annotation specifies a connection to another capsule that this capsule does not initialize. The parent of the
 * capsule template with the @Imports field must supply the reference via a design method call. 
 * <p>
 * The difference between @Local and @Imports is that the @Local reference is created and managed by the capsule that includes it as a field. An @Imports 
 * requires that the reference be passed into the capsule at initialization time.
 * <h3>Exceptions</h3>
 * <p>
 * None
 * <h3>Examples</h3>
 * <p>
 * In this example, we have three capsules, the first is a passive capsule named Console, the second is a passive capsule named Greeter and the third is an active capsule called HelloWorld. 
 * The active capsule HelloWorld holds a connection to the passive capsules Greeter and Console in order to call on its procedures. This connection is
 * set up by the @Local annotation on the Greeter and Console field of the HelloWorldTemplate. 
 * <p>
 * The capsule Greeter has a field of type Console that is annotated with the @Imports. This means
 * that the Greeter wants to call procedures of a Console capsule, but does not want to create the instance of the capsule. By using @Imports, it specifies that the parent capsule of the Greeter
 * must supply the reference to the Console capsule. This is shown in the HelloWorldTemplate in the design method where g.imports is called and the reference to the Console capsule is passed as
 * a parameter.  
 * <h4>ConsoleTemplate.java</h4>
 * <blockquote><pre>
 * &#64;Capsule
 * public class ConsoleTemplate {
 *     
 *     &#64;Block
 *     public void write(String s) {
 *         System.out.println(s);
 *     }
 * }
 * </pre></blockquote>
 * <h4>GreeterTemplate.java</h4>
 * <blockquote><pre>
 * &#64;Capsule
 * public class GreeterTemplate {
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
 * <h4>HelloWorldTemplate.java</h4>
 * <blockquote><pre>
 * &#64;Capsule
 * public class HelloWorldTemplate {
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
public @interface Imports { /* No annotation arguments. */ }
