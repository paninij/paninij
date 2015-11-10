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
 * <p>
 * Used to declare a procedure as 'ducked' behavior.
 * <h3>Purpose</h3>
 * The @Duck annotation specifies that a procedure will return a duckified result.
 * <h3>Details</h3>
 * <p>
 * The returned duck will extend the class of the return type in order to provide an
 * invisible future to the caller of the procedure. The duck version of the 
 * return type will override all methods of the extended class. The override versions
 * block the execution of the thread and wait for the
 * actual result to be finished. If the result is finished prior to the usage,
 * the invocation of the method on the returned object will execute normally.
 * <h3>Exceptions</h3>
 * <p>
 * Objects that are final are unable to be duckified thus procedures that 
 * return such objects are unable to be annotated @Duck.
 * <h3>Examples</h3>
 * <p>
 * In this example, the procedure compute returns an object of type Number.
 * Number is then duckified into a panini internal class org_paninij_examples_pi_Number$Duck$dbl
 * which extends the Number class and implements Panini$Future{@literal <}Number{@literal >}. 
 * <p>
 * When this procedure is called, the duck version is immediately returned allowing the main thread
 * to continue executing. 
 * <blockquote><pre>
 * &#64;Duck
 * public Number compute(double num) {
 *   Number _circleCount = new Number();
 *     for (double j = 0; j{@literal <} num; j++) {
 *     double x = this.prng.nextDouble();
 *     double y = this.prng.nextDouble();
 *     if ((x * x + y * y){@literal <} 1) _circleCount.incr();
 *   }
 *   return _circleCount;
 * }
 * </pre></blockquote>
 * <h3>Internal Notes</h3>
 * <p>
 * The naming convention of the resulting duck takes into account the fully qualified origin of the
 * return type and the signature of the template method. This reduces the number of ducks created for 
 * some capsule templates while ensuring that similarly named return types do not clash.
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
 * 
 *
 */
public @interface Duck { }
