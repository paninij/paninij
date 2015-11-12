/**
 * <p>The <code>org.paninij.lang</code> package offers various classes and annotations which are used by a
 * developer to create an &#64;PaniniJ program. &#64;PaniniJ is an annotation-based realization of capsule-oriented programming in Java. Capsule-oriented programming is an implicitly concurrent programming model.
 * 
 * <p>The annotations defined within this package are needed to define a &#64;PaniniJ program. The
 * user writes a set of Java class definitions, but they are annotated in such a way that they can
 * be <em>interpreted</em> as a valid &#64;PaniniJ program.
 * 
 * 
 * <h2>Example 1: Defining a Capsule</h2>
 * <h3>HelloWorldTemplate.java</h3>
 * <blockquote><pre>
 * package example;
 * 
 * import org.paninij.lang.*;
 * 
 * &#64;Root
 * &#64;Capsule
 * public class HelloWorldTemplate
 * {
 *     public void run() {
 *         System.out.println("Hello world");
 *     }
 *     
 *     public static void main(String[] args) {
 *         CapsuleSystem.start(HelloWorld.class, args);
 *     }
 * }
 * </pre></blockquote>
 * 
 * 
 * <p>Example 1 creates a <em>capsule template</em> named <code>HelloWorldTemplate</code>. The
 * &#64;PaniniJ annotation processor will see the <code>&#64;Capsule</code> annotation and will
 * automatically generate an interface named "HelloWorld" to represent the capsule itself.</p>
 * 
 * <p>A capsule is a modular component that encapsulates state and behavior. Individual capsules are
 * linked to one another to form a capsule system. Every capsule is meant to be able to execute
 * concurrently (i.e. simultaneously). A capsule is not allowed to share it's state with others in
 * a system to prevent unsafe concurrency (e.g. data races).</p>
 * 
 * <p>Certain methods are given special meanings in &#64;PaniniJ. For example, a <code>run()</code>
 * method (a.k.a. run declaration) defines the entire execution behavior for a capsule. The
 * <code>run()</code> method runs once from beginning to end, and when it returns, the capsule
 * terminates. This behavior is similar to Java's <code>Thread.run()</code>.</p>
 * 
 * <p>A Capsule with a <code>run()</code> method is called an <em>active</em> capsule. Capsules
 * without a <code>run()</code>, are called <em>passive</em> capsules. (More on them below.)</p>
 * 
 * <p>Because we added the <code>&#64;Root</code> annotation to <code>HelloWorldTemplate</code>,
 * the <code>HelloWorld</code> capsule can be treated as a <em>root</em> capsule. Because of this,
 * we can start a capsule system rooted at the <code>HelloWorld</code> capsule by calling
 * <code>CapsuleSystem.start(HelloWorld.class, args);</code>.</p>
 * 
 * Running the <code>main()</code> method in example will start up and run a capsule system, but it
 * isn't much of a system since `HelloWorldTemplate` does not link to any other capsules: the
 * capsule system will just contain a single capsule instance. The following example illustrates a
 * (slightly) larger system of capsules.
 * 
 * 
 * <h2>Example 2: A System of Capsules</h2>
 * <h3>ConsoleTemplate.java</h3>
 * <blockquote><pre>
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
 * </pre></blockquote>
 * 
 * <h3>GreeterTemplate.java</h3>
 * <blockquote><pre>
 * package example;
 * 
 * import org.paninij.lang.*;
 * 
 * &#64;Root
 * &#64;Capsule
 * public class GreeterTemplate
 * {
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
 * </pre></blockquote>
 * 
 * 
 * <p>Example 2 gives an example of how to make a system with two capsules, <code>Greeter</code> and
 * <code>Console</code>. The example defines two capsule templates (each annotated with
 * <code>&#64;Capsule</code>): <code>GreeterTemplate</code> and <code>ConsoleTemplate</code>.
 * 
 * Similarly to before, each template will be used to automatically generate corresponding capsule
 * types (in this case <code>Greeter</code> and <code>Console</code>) and the system can be started
 * using <code>CapsuleSystem.start(Greeter.class, args)</code>. (Note that <code>Console</code>
 * cannot be used to start a capsule system, because only <code>&#64;Root</code>-annotated capsules
 * can be started.)
 * 
 * <p>Unlike <code>HelloWorld</code> and <code>Greeter</code>, the <code>Console</code> has no
 * <code>run()</code> method. We thus call <code>Console</code> a <em>passive</em> capsule. Those
 * methods of a passive capsule which are not special methods* are called <em>procedures</em>.
 * <code>Console</code> has one procedure, <code>say()</code>.</p>
 * 
 * <p>Notice that inside of the <code>run()</code> method, we invoke the <code>say()</code>
 * procedure on the instance of <code>Console</code>. The syntax looks like a method call on an
 * object, but in &#64;PaniniJ, this actually causes in a message to be sent from the
 * <code>Greeter</code> to the <code>Console</code>. Packaged inside of this message will be our
 * <code>String message</code> to be used as the parameter of <code>say()</code> when it is
 * executed.</p>
 * 
 * <p>It may be useful to think of each procedure as defining a different "handler" that is made
 * publicly available to other capsules. Any capsule (active or passive) can <em>invoke</em> the
 * procedures of a passive capsule. An invocation is like a request being sent to a passive capsule.
 * Each request is handled one at a time in the order in which they arrive (FIFO).</p>
 * 
 * <p>So, unlike an active capsule which executes its <code>run()</code> method once to completion,
 * execution within a passive capsule is instead driven by the particular requests sent by other
 * capsules. Note that passive capsules only terminate when they have no more requests to process
 * and when they cannot possibly be sent more requests. (This termination should be handled by the
 * runtime at some point in the future, but it is not yet implemented.)</p>
 * 
 * <p>Notice that the <code>Greeter</code> capsule refers to an instance of <code>Console</code>
 * which doesn't seem to be initialized anywhere in the example. This is because its initialization
 * will be done automatically by &#64;PaniniJ. (The <code>&#64;Local</code> annotation indicates
 * the way in which a capsule instance field should be initialized, but more on that later.)</p>
 * 
 * <p><strong>WARNING:</strong>You should always allow the &#64;PaniniJ runtime to be responsible
 * for performing capsule instance initialization. The manual initialization or creation of capsule
 * instances is undefined behavior.</p>
 * 
 * The capsule system described in Example 2 is hardly any more complex than the system in Example
 * 1. The next example explains how to construct more complicated capsule systems by introducing the
 * concept of capsule imports.
 * 
 * 
 * <h2>Example 3: Ping-Pong</h2>
 * <h3>PingTemplate.java</h3>
 * <blockquote><pre>
 * package example;
 * 
 * import org.paninij.lang.*;
 * 
 * &#64;Capsule
 * public class PingTemplate
 * {
 *     // A Ping capsule needs a reference to a Pong capsule.
 *     &#64;Imports Pong pong;
 *     
 *     // A Ping capsule also needs to know the number of pings to send.
 *     &#64;Imports int count;
 * 
 *     public void ping() {
 *         if (--count > 0) {
 *             pong.pong(count);
 *         } else {
 *             System.out.println("done");
 *             // Manual shutdown, since there is a circular dependency.
 *             pong.exit();
 *         }
 *     }
 * }
 * </pre></blockquote>
 * 
 * <h3>PongTemplate.java</h3>
 * <blockquote><pre>
 * package example;
 * 
 * import org.paninij.lang.*;
 * 
 * &#64;Capsule
 * public class PongTemplate
 * {
 *     // The Pong template needs a reference to a Ping capsule.
 *     &#64;Imports Ping ping;
 * 
 *     public void pong(int n) {
 *         ping.ping();
 *         if (n % 1000 == 0) {
 *             System.out.println("count = " + n);
 *         }
 *     }
 * }
 * </pre></blockquote>
 *
 * <h3>PingPongTemplate.java</h3>
 * <blockquote><pre>
 * package example;
 * 
 * import org.paninij.lang.*;
 * 
 * &#64;Root
 * &#64;Capsule
 * public class PingPongTemplate
 * {
 *     // Declare an instance of a Ping capsule and another of a Pong capsule.
 *     &#64;Local Ping ping;
 *     &#64;Local Pong pong;
 *     
 *     // Inject the dependencies for each capsule. Notice that the `self`
 *     // argument isn't used in this case, because neither needs a reference
 *     // to the PingPong capsule itself.
 *     public void design(PingPong self) {
 *         ping.imports(pong, 100000);
 *         pong.imports(ping);
 *     }
 *     
 *     // We invoke an initial `ping` procedure on the ping capsule.
 *     public void run() {
 *         ping.ping();
 *     }
 *     
 *     // Start a capsule system with a PingPong capsule as the root.
 *     public static void main(String[] args) {
 *         CapsuleSystem.start(PingPong.class, args);
 *     }
 * }
 * </pre></blockquote>
 *
 * <p>Example 3 creates three capsules: <code>Ping</code>, <code>Pong</code>, and
 * <code>PingPong</code>. Notice that the <code>Ping</code> and <code>Pong</code> capsules rely on
 * each other (see the fields annotated with <code>&#64;Imports</code>). We'll explain how each is
 * initialized the references it needs.</p>
 * 
 * <p>The <code>PingPong</code> capsule (which is both active and root) includes two
 * <code>&#64;Local</code> fields. These indicate to the runtime that both a <code>Ping</code> and a
 * <code>Pong</code> should be instantiated, initialized, and assigned to these fields.</p>
 * 
 * <p><code>PingPong</code> includes two other "special" methods which we haven't yet described:
 * <code>design()</code> and <code>imports()</code>. The body of <code>PingPong.design()</code> uses
 * calls to <code>imports()</code> to give both <code>Ping</code> and <code>Pong</code> a reference
 * to the other.</p>
 * 
 * <p>This designing and wiring happens during a capsule's initialization, that is, before a capsule
 * starts executing "normally". Once <code>PingPong</code> is initialized, it will start executing
 * its <code>run()</code> method, which simply invokes an initial <code>ping</code> message on the
 * <code>Ping</code> capsule and then terminates. This is enough to start off a chain reaction
 * between the two passive capsules. They communicate back and forth 100000 times and then exit.</p>
 * 
 * 
 * <h2>Special Declarations</h2>
 * 
 * <p>Much like how a <code>main()</code> method has a special meaning in Java, there are a few
 * capsule method declarations which are treated specially in &#64;PaniniJ.
 * 
 * <dl>
 * <dt>public void run()</dt>
 * <dd>
 * 		<p>The main entry point of an active capsule. The active capsule runs this code once
 *              to completion. If a capsule contains a run method, it cannot contain any other
 *              procedures.</p>
 * </dd>
 * <dt>public void init()</dt>
 * <dd>
 * 		<p>This method can be used to populate state variables. It is called before run.
 *              This should be used instead of a constructor.</p>
 * </dd>
 * <dt>public void design(Self self)</dt>
 * <dd>
 * 		<p>The design method is used specify how the capsules within a system should be
 *              linked together. If any of a capsule's <code>&#64;Local</code> capsule fields
 *              require imports, the appropriate calls to <code>imports()</code> must be performed
 *              within <code>design()</code>. (This is where dependency injection is performed).</p>
 * </dd>
 * <dt>public void imports(...)</dt>
 * <dd>
 * 		<p>The imports method is auto-generated on a Capsule interface. This method's
 *              parameter list matches the list sequence of fields annotated with &#64;Import. (This
 *              is the means by which the <code>&#64;Import</code> dependencies are injected.</p>
 * </dd>
 * <dt>public void exit()</dt>
 * <dd>
 * 		<p>TODO</p>
 * </dd>
 * </dl>
 * 
 * <h2>Definitions</h2>
 * <dl>
 * <dt>Capsule Template</dt>
 * <dd>
 * 		<p>User-written code which defines a capsule.</p>
 * </dd>
 * <dt>Capsule</dt>
 * <dd>
 * 		<p>A modular component that encapsulates state and behavior. The classes which
 *              implement a capsule are automatically generated based on the user-defined capsule
 *              templates.</p>
 * </dd>
 * <dt>Capsule System</dt>
 * <dd>
 * 		<p>A collection of concurrently running capsules which work together. A system is
 *              started with respect to a single <em>root</em> capsule.</p>
 * </dd>
 * <dt>Active Capsule</dt>
 * <dd>
 * 		<p>A Capsule which contains a <code>run()</code> method and no other procedures.
 *              These capsules essentially drive an entire capsule system.</p>
 * </dd>
 * <dt>Passive Capsule</dt>
 * <dd>
 * 		<p>A non-active capsule. These capsules handle procedure invocations in a FIFO
 *              manner.</p>
 * </dd>
 * <dt>Root Capsule</dt>
 * <dd>
 * 		<p>A capsule from which a capsule system system can be started. (Often also an
 *              active capsule.) These capsules cannot have any <code>&#64;Imports</code> fields
 *              (i.e. they cannot have any dependencies).
 * </dd>
 * <dt>Procedure</dt>
 * <dd>
 * 		<p>A non-special* method on a Capsule, these are defined in the capsule templates
 *              of passive capsule. Capsules can invoke procedures on one another.
 * </dd>
 * <dt>Signature</dt>
 * <dd>
 * 		<p>An Interface for Capsules.
 * </dd>
 * </dl>
 * 
 * <p><em>*A special method is a method with one of the following names:
 * <code>run()</code>, <code>init()</code>, <code>design()</code>, and <code>imports()</code></em>.
 * 
 * <h2>Academic References</h2>
 * <ol>
 * <li>H. Rajan, S. M. Kautz, E. Lin, S. L. Mooney, Y. Long, and G. Upadhyaya, "Capsule-oriented programming in the Panini language," Iowa State University, Tech. Rep. 14-08, 2014.</li>
 * <li>H. Rajan, "Capsule-oriented Programming," ICSE'15: The 37th International Conference on Software Engineering, Florence, Italy, May 2015.</li>
 * </ol>
 */
package org.paninij.lang;