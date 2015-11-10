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
 * Contributor(s): Dalton Mills
 */
package org.paninij.lang;

/**
 * <h3>Purpose</h3>
 * <p>
 * This annotation is used to specify the behavior of the procedure that it annotates. When a procedure is annotated with @Block it will not be futurized and instead will stop
 * the execution on the calling thread until the procedure has completed its execution.
 * <h3>Details</h3>
 * <p>
 * A procedure annotated with @Block will not return either type of future and will instead only return the actual fully computed result. As such, the capsule that sent the message
 * will block execution until the answer is returned. @Block works for any return type as there is no type mangling going on behind the scenes. The procedure will act similarly to a
 * java method called sequentially.
 * <h3>Exceptions</h3>
 * <p>
 * There are no restrictions on what procedures can be annotated with @Block.
 * None
 * <h3>Examples</h3>
 * <p>
 * In this example we will use two capsules, one that is passive and has a procedure that returns an int and another that is active and holds a link to the first capsule. 
 * The passive capsule, Doubler, will have its procedure annotated with @Block which will cause the execution in the active capsule, Runner, to halt until the procedure is 
 * finished. This will ensure that there are no data races when the variable i is modified later in the execution. It will also have the advantage that the procedure will
 * return the primitive type int and no extra future wrangling is needed to use the result.
 * <blockquote><pre>
 * &#64;Capsule
 * public DoublerTemplate {
 *     
 *     &#64;Block
 *     public int doubleIt(int x) {
 *         return x * 2;
 *     }
 * }
 * </pre></blockquote>
 * <blockquote><pre>
 * &#64;Capsule
 * public RunnerTemplate {
 *     &#64;Local Doubler d;
 *     
 *     void run() {
 *         int i = 4;
 *         System.out.println("The number " + i + " doubled is " + d.doubleIt(i) + "!"); // "The number 4 doubled is 8!"
 *         i = d.doubleIt(i);
 *         System.out.println("The number " + i + " doubled is " + d.doubleIt(i) + "!"); // "The number 8 doubled is 16!"
 *     }
 * 
 * }
 * </pre></blockquote>
 * <h3>Internal Notes</h3>
 * <p>
 * None
 * <h3>Associated Annotations</h3>
 * <p>
 * This annotation is one of the three which can define the behavior
 * of a procedure. They are:
 * <ul>
 * <li>	{@link org.paninij.lang.Block @Block} - blocking behavior</li>
 * <li> {@link org.paninij.lang.Duck @Duck} - futurized behavior</li>
 * <li> {@link org.paninij.lang.Future @Future} - ducked behavior</li>
 * </ul>
 *
 */
public @interface Block { }
