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

import java.lang.annotation.Documented;

/**
 * <p>
 * Used to declare a procedure as 'futurized' behavior.
 * <h3>Purpose</h3>
 * The &#64;Future annotation specifies that a procedure will return a result that implements the Future{@literal <}T{@literal >} interface 
 * where T is the type specified by the template method's return type.
 * <h3>Details</h3>
 * <p>
 * This annotation is used in cases where the type returned by the procedure is unable to be ducked but the flexibility
 * to continue executing is desired. These cases include procedures that have return types which: are final, are classes 
 * that override the default constructor, are arrays, or are primitives.
 * <p>
 * The generated code for this procedure will instead use the {@link java.util.concurrent.Future Future}
 * interface to produce an explicit future to facilitate the continued execution. This
 * means that the method or procedure consuming the explicit future must handle it as such and resolve the future using the
 * get method from the Future interface. If the result of the future has not finished being computed, the execution will 
 * be blocked until the result is available. 
 * <p>
 * This annotation will work for procedures that have primitive or void return types. In the case of a primitive, it will return
 * a future of the promoted, object version of the primitive. In the case of int, a Future{@literal <}Integer{@literal >} will be 
 * returned. In the case of
 * void, the get method of the future can be used to ensure that the procedure that returned the future has fully executed. 
 * <h3>Exceptions</h3>
 * <p>
 * None
 * <h3>Examples</h3>
 * <p>
 * In this example we have two capsule templates. The first being SquarerTemplate, which has one procedure annotated with
 * &#64;Future. The second is the MathDemonstratorTemplate, which is linked to the Squarer capsule and has a run method that
 * calls on the getSquare procedure of Squarer.
 * <p>
 * One key point of this example is the promotion of the primitive type that the getSquare procedure returns from int to
 * Integer. The code that consumes the future that is returned must also take this into account when they resolve the
 * future.
 * <p>
 * It is important to see that the execution in run() does not stop when getSquare is called. it will continue on executing
 * concurrently until it attempts to resolve the future. If for some reason the getSquare procedure has not finished executing
 * by the time the run method executes result.get(), execution on run() will be blocked until the result is obtained. This means
 * that the final output will never come before the output that uses the result of the getSquare procedure, ensuring the ordering
 * while still utilizing the benefits of concurrency.
 * 
 * <blockquote><pre>
 * &#64;Capsule
 * public SquarerTemplate {
 * 
 *     &#64;Future
 *     public int getSquare(int x) {
 *         return x*x;
 *     }
 * }
 * </pre></blockquote>
 * Example Caller of &#64;Future procedure.
 * <blockquote><pre>
 * &#64;Capsule
 * public MathDemonstratorTemplate {
 *     &#64;Local Squarer squarer;
 * 
 *     public run() {
 *         int i = 2;
 *         Future{@literal <}Integer{@literal >} result = squarer.getSquare(i);
 *         int j = i * 2;
 *         System.out.println("The result of " + i + " times 2 is: " + j + "!");
 *         try {
 *             System.out.println("The result of " + i + " squared is: " + result.get() + "!");
 *         } catch (ExecutionException ex) {
 *         	//.. handle exception
 *         }
 *         System.out.println("I hope you enjoyed the math tricks that I have shown you today... :^)");
 *     }
 * }
 * </pre></blockquote>
 * <h3>Internal Notes</h3>
 * <p>
 * This annotation is used to cover the cases in which the return type of the procedure cannot be duckified. Such cases include:
 * final classes, classes that override the zero argument constructor, and arrays. If a procedure is not annotated, the annotation
 * processor will never assign the procedure as @Future. 
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
@Documented
public @interface Future {}
