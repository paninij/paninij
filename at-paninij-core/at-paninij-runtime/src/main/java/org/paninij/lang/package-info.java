/**
 * <p>The org.paninij.lang package offers various classes and annotations
 * which are used by a developer to create a Panini program.</p>
 * 
 * <h2>Example 1: Creating a capsule</h2>
 * <p><blockquote><pre>
 * package example;
 * 
 * import org.paninij.lang.*;
 * 
 * &#64;Root
 * &#64;Capsule
 * public class HelloWorldTemplate {
 *     public void run() {
 *         System.out.println("Hello world");
 *     }
 *     
 *     public static void main(String[] args) {
 *         CapsuleSystem.start(HelloWorld.class, args);
 *     }
 * }
 * </pre></blockquote></p>
 * 
 * <p>Example 1 creates a Capsule Template called HelloWorldTemplate. &#64PaniniJ will
 * see the annotation <code>&64;Capsule</code> and will auto-generate a class called "HelloWorld"
 * which represents the Capsule. A capsule is a modular component that encapsulates state and behavior. A Capsule is not 
 * allowed to share it's state with others. Because of the <code>&#64;Root
 * </code> annotation and the <code>run</code> method, this is an
 * <em>active</em> capsule, and we can start it by calling <code>CapsuleSystem.start(HelloWorld.class, args);</code>. The <code>run</code> method is the main point of
 * entry for this system.</p>
 * 
 * <h2>Example 2: A system of capsules</h2>
 * <h4>ConsoleTemplate.java</h4>
 * <p><blockquote><pre>
 * package example;
 * 
 * import org.paninij.lang.*;
 * 
 * &#64;Capsule
 * public class ConsoleTemplate {
 *     public void say(String message) {
 *         System.out.println(message);
 *     }
 * }
 * </pre></blockquote></p>
 * 
 * <h4>GreeterTemplate.java</h4>
 * <p><blockquote><pre>
 * package example;
 * 
 * import org.paninij.lang.*;
 * 
 * &#64;Root
 * &#64;Capsule
 * public class GreeterTemplate {
 *     &#64;Local Console console;
 *     
 *     public void run() {
 *         console.say("Hello world");
 *     }
 *     
 *     public static void main(String[] args) {
 *         CapsuleSystem.start(Greeter.class, args);
 *     }
 * }
 * </pre></blockquote></p>
 * 
 * <p>Example 2 creates two capsules: Greeter and Console. The Console
 * capsule has one <em>procedure</em> called <code>say</code>. The
 * Greeter capsule has an instance of Console called "console" (<code>&#64;Local
 * Console console</code>). Notice that console isn't initialized anywhere. This is done automatically by &#64;Local. Note that the Greeter capsule is an
 * <em>active</em> capsule because it contains a <code>run</code>
 * method. Inside the <code>run</code> method, we invoke the <code>say
 * </code> procedure on the instance of <code>Console</code> with a
 * message.
 * 
 * 
 * <h2>Definitions</h2>
 * <dl>
 * <dt>Capsule Template</dt>
 * <dd>
 * 		<p>User-written code to define a capsule. See
 * </dd>
 * <dt>Capsule</dt>
 * <dd>
 * 		<p>A modular component that encapsulates state and behavior.
 * </dd>
 * <dt>Capsule System</dt>
 * <dd>
 * 		<p>A collection of capsules which work together, and has one <em>root</em> capsule.
 * </dd>
 * <dt>Active Capsule</dt>
 * <dd>
 * 		<p>A Capsule which contains a <code>run</code> method and no other procedures. This capsule is a driver behind other capsules.
 * </dd>
 * <dt>Passive Capsule</dt>
 * <dd>
 * 		<p>A non-active capsule.
 * </dd>
 * <dt>Root Capsule</dt>
 * <dd>
 * 		<p>The primary capsule in the system, typically also an Active capsule. This capsule cannot have any dependencies (IE, no <code>&#64;Imports</code> fields)
 * </dd>
 * <dt>Procedure</dt>
 * <dd>
 * 		<p>A non-special* method on a Capsule, these are defined in the Capsule Template. Capsules can invoke procedures on eachother.
 * </dd>
 * <dt>Signature</dt>
 * <dd>
 * 		<p>An Interface for Capsules.
 * </dd>
 * </dl>
 * 
 * <p><em>*non-special: Meaning not <code>run</code>, <code>init</code>, <code>design</code>, or <code>imports</code></em></p>
 * 
 * 
 * 
 * <p>The primary driver behind &#64;PaniniJ is the annotations within this
 * package.
 * 
 */
package org.paninij.lang;