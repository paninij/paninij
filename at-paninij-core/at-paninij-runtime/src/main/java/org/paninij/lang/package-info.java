/**
 * <p>The org.paninij.lang package offers various classes and annotations
 * which are used by a developer to create a Panini program.</p>
 * 
 * <p>The primary driver behind writing &#64;PaniniJ is the annotations within this
 * package.
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
 * <p>Example 1 creates a Capsule Template called HelloWorldTemplate. &#64;PaniniJ will
 * see the annotation <code>&#64;Capsule</code> and will auto-generate a class called "HelloWorld"
 * which represents the Capsule. A capsule is a modular component that encapsulates state and behavior. A Capsule is not 
 * allowed to share it's state with others. Because of the <code>&#64;Root
 * </code> annotation, this is a <em>root</em> capsule, and we can start it by calling <code>CapsuleSystem.start(HelloWorld.class, args);</code>.
 * The <code>run</code> method has special meaning in &#64;PaniniJ - it is the main point of
 * entry for an active capsule.</p>
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
 * <p>Example 2 creates two capsules: Greeter and Console. This was done by creating two classes: GreeterTemplate and ConsoleTemplate.
 * The Console capsule has one <em>procedure</em> called <code>say</code>. The
 * Greeter capsule has an instance of Console called "console" (<code>&#64;Local
 * Console console;</code>). Notice that console isn't initialized anywhere (and there are no constructors). Initialization is done automatically by &#64;Local.
 * Inside the <code>run</code> method, we invoke the <code>say
 * </code> procedure on the instance of <code>Console</code> with a
 * message. The System is started off by calling <code>CapsuleSystem.start(Greeter.class, args);</code>.
 * Only <em>root</em> capsules can be started.
 * 
 * <h2>Example 3: Ping Pong</h2>
 * <h4>PingTemplate.java</h4>
 * <p><blockquote><pre>
 * package example;
 * 
 * import org.paninij.lang.*;
 * 
 * &#64;Capsule
 * public class PingTemplate {
 *     // the Ping capsule needs a reference to a Pong capsule
 *     &#64;Imports Pong pong;
 *     
 *     // the Ping capsule will also be provided an int `count` (the number of pings to send)
 *     &#64;Imports int count;
 * 
 *     public void ping() {
 *         if (--count > 0) {
 *             pong.pong(count);
 *         } else {
 *             System.out.println("done");
 *             // Since there is a circular dependency, we have to manually shut down the pong capsule
 *             pong.exit();
 *         }
 *     }
 * }
 * </pre></blockquote></p>
 * 
 * <h4>PongTemplate.java</h4>
 * <p><blockquote><pre>
 * package example;
 * 
 * import org.paninij.lang.*;
 * 
 * &#64;Capsule
 * public class PongTemplate {
 *     // the Pong template needs a reference to a Ping capsule
 *     &#64;Imports Ping ping;
 * 
 *     public void pong(int n) {
 *         ping.ping();
 *         if (n % 1000 == 0) System.out.println("count = " + n);
 *     }
 * }
 * </pre></blockquote></p>
 *
 * <h4>PingPongTemplate.java</h4>
 * <p><blockquote><pre>
 * package example;
 * 
 * import org.paninij.lang.*;
 * 
 * &#64;Root
 * &#64;Capsule
 * public class PingPongTemplate {
 *     // we create a local instance of the Ping capsule
 *     &#64;Local Ping ping;
 *     
 *     // and a local instance of the Pong capsule
 *     &#64;Local Pong pong;
 *     
 *     // we populate (inject) the dependencies for each capsule
 *     // notice that the `self` argument isn't used in this case.
 *     public void design(PingPong self) {
 *         ping.imports(pong, 100000);
 *         pong.imports(ping);
 *     }
 *     
 *     // we send invoke the inital `ping` procedure on the ping capsule
 *     public void run() {
 *         ping.ping();
 *     }
 *     
 *     // we start the PingPong class
 *     public static void main(String[] args) {
 *     		CapsuleSystem.start(PingPong.class, args);
 *     }
 * }
 * </pre></blockquote></p>
 *
 * <p>Example 3 creates three capsules: Ping, Pong, and PingPong. The Ping and Pong capsules rely on eachother (see the fields annotated with &#64;Imports).
 * The PingPong (active, and root) capsule creates an instance of Ping and Pong, and it wires them together in the <code>design</code> method. The <code>design</code>
 * method is a special declaration (see below) just like the <code>run</code> method. It then invokes an initial <code>ping</code> message on the Ping capsule. These capsules
 * communicate back and forth 100000 times and then exit. 
 * 
 * 
 * <h2>Special Declarations</h2>
 * <p>Much like how <code>main</code> has special meaning in Java, there are a few special method declarations for capsules in &#64;PaniniJ.
 * 
 * <dl>
 * <dt>public void run()</dt>
 * <dd>
 * 		<p>The main entry point of an active capsule. If a capsule contains a run method, it cannot contain any other procedures.
 * </dd>
 * <dt>public void init()</dt>
 * <dd>
 * 		<p>This method can be used to populate state variables. It is called before run.
 * </dd>
 * <dt>public void design(Self self)</dt>
 * <dd>
 * 		<p>The design method is used to <em>wire</em> capsules together (to create a system). In the design method, you can populate the
 * 		 &#64;Import fields of other capsules (injecting the dependencies).
 * </dd>
 * <dt>public void imports(Other other..)</dt>
 * <dd>
 * 		<p>The imports method is auto-generated on a Capsule class. It is a way to inject the dependencies defined by &#64;Import. Notice that the arguments to this method depend on which fields are annotated with &#64Import.
 * </dd>
 * <dt>public void exit()</dt>
 * <dd>
 * 		<p>The imports method is auto-generated on a Capsule class. It is a way to inject the dependencies defined by &#64;Import.
 * </dd>
 * </dl>
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
 * <p><em>*non-special: Meaning not <code>run</code>, <code>init</code>, <code>design</code>, or <code>imports</code></em>
 * 
 * 
 * 
 */
package org.paninij.lang;