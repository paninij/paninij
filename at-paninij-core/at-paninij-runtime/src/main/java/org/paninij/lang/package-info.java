/**
 * <p>The org.paninij.lang package offers various classes and annotations which
 * are used by a developer to create a Panini program.</p>
 * 
 * <h2>Example 1: Creating a capsule</h2>
 * <p><blockquote><pre>
 * &#64;Capsule public class HelloWorldTemplate {
 *     public void run() {
 *         System.out.println("Hello world");
 *     }
 * }
 * </pre></blockquote></p>
 * 
 * <p>Example 1 creates a Capsule Template called HelloWorld. A capsule is a 
 * modular component that encapsulates state and behavior. A Capsule is not 
 * allowed to share it's state with others. Because of the <code>run</code> 
 * method, this is an <em>active</em> capsule. The <code>run</code> method
 * is the main point of entry for this system. </p>
 * 
 * <h2>Example 2: A system of capsules</h2>
 * <p><blockquote><pre>
 * &#64;Capsule public class ConsoleTemplate {
 *     public void say(String message) {
 *         System.out.println(message);
 *     }
 * }
 * 
 * &#64;Capsule public class GreeterTemplate {
 *     &#64;Local Console console;
 *     
 *     public void run() {
 *         console.say("Hello world");
 *     }
 * }
 * </pre></blockquote></p> 
 * <p>Example 2 creates two capsules, Greeter and Console. The Console capsule
 * has one <em>procedure</em> called <code>say</code>. The Greeter capsule
 * creates a Console called "console" (<code>&#64;Local Console console</code>
 * ). Note that the Greeter capsule is an <em>active</em> capsule because it
 * contains a <code>run</code> method. Inside the <code>run</code> method, we 
 * invoke the <code>say</code> procedure on the instance of <code>Console</code>
 * with a message.</p>
 * 
 * <p>The primary driver behind @PaniniJ is the annotations within this package.</p>
 * 
 * <h3>Annotations used to declare a Capsule Template:</h3>
 * <ul>
 * <li>org.paninij.lang.Capsule - to create a Capsule Template</li>
 * </ul>
 * 
 * <h3>Annotation used to declare a Signature Template:</h3>
 * <ul>
 * <li>org.paninij.lang.Signature - to create a Signature Template</li>
 * </ul>
 * 
 * <h3>Annotations used on fields in a Capsule Template:</h3>
 * <ul>
 * <li>org.paninij.lang.Imports - to declare imported fields on a capsule</li>
 * <li>org.paninij.lang.Local - to declare local fields on a capsule</li>
 * <li>org.paninij.lang.Block - to declare a procedure as 'blocking' behavior</li>
 * <li>org.paninij.lang.Future - to declare a procedure as 'futurized' behavior</li>
 * <li>org.paninij.lang.Duck - to declare a procedure as 'ducked' behavior</li>
 * </ul>
 * 
 */
package org.paninij.lang;