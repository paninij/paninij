---
layout: home
title: "@PaniniJ"
---

**@PaniniJ** is a *Java compiler plugin* to support **capsules**, a safer, more
modular way to write concurrent components.
{: .lead}

<a id="get-started-link" href="/man/p1/ch2_getting_started.html">
  <button type="button" class="btn btn-primary btn-lg btn-block">Get Started</button>
</a>


### Concurrent Execution, Asynchronous Communication

Capsules are specified by writing annotated Java classes:

``` java
@Capsule
class HelloWorldCore {
    public void run() {
        System.out.println("Hello, world.");
    }
}
```
{: .code-with-line-numbers}

These annotations let's us set up a network of capsules reacting to each other.

**Concurrency:** Capsules within the graph run simultaneously. They send
messages to one another using method-call syntax. These messages usually
represent an asynchronous task that one capsule wants another capsule to
perform.

**Transparent Futures:** The result of an asynchronous task can be returned via
a *transparent future*. The future looks like the object that we want, but it is
returned immediately, even if the task takes longer. This gives the client the
option to either.

1. Ignore the result.
2. Immediately wait for the result.
3. Do some work, then wait for the result.
4. Or just pass it on to another capsule.

Here's an example of option 3:

``` java
import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule
public class MyClientCore {
    @Local Graph graphA;
    @Local Graph graphB;
    void run() {
        String msg;

        // Start two costly operations. Get futures their results.
        Path pathAB = graphA.shortestPath("A", "B");
        Path pathYZ = graphA.shortestPath("Y", "Z");

        // Do more work, even though tasks may not be done.
        // ...

        if (pathAB.length() < pathYZ.length()) {        // Waits if necessary.
            msg = "Path A --> B was shorter."
        } else if (pathAB.length() > pathYZ.length()) { // No waiting here.
            msg = "Path Y --> Z was shorter."
        } else {
            msg = "The paths have the same length."
        }

        System.out.println(msg);
    }
}

@Capsule
class GraphCore {
    // Encapsulated graph state.
    // ...
    public Path shortestPath(String vertexA, String vertexB) {
        // A sequential implementation of a shortest path algorithm.
        // ...
    }
}
```
{: .code-with-line-numbers}

Because calls to `Graph#shortestPath()` are asynchronous, the client doesn't
wait for the `pathAB` result before making a request to get `pathYZ`. It is only
later, where the `Path#length()` methods are called that the client will need to
wait. Subsequent calls to the `Path` futures won't block.

The use of futures, let's the do some more work while waiting for the results
from the graphs.


### Developed with Standard Java Tools

@PaniniJ provides these (and many other) features by extending the Java compiler
to write Java code for capsules themselves. The capsules wrap the capsule cores
and manage their interactions in a thread-safe way.

Additionally, there are a number of rules that statically restrict how a capsule
can be defined. Many of these rules are checked by our compiler plugin. If a
check fails, the error is reported just like an ordinary Java compiler error.

<div class="row">
<div class="col-md-12">
<img src="/img/eclipse_error_message.png"
     class="img-fluid"
     alt="An @PaniniJ-specific contextual error message seen in Eclipse">
</div>
</div>
*An @PaniniJ-specific contextual error message as seen in Eclipse.*
{: .pull-right}


Our standards-compliant compiler plugin is well-supported by many standard Java
tools: `javac`, Maven, Gradle, Eclipse, Netbeans, and IntelliJ IDEA. Often,
adding @PaniniJ to your Java project is as simple as adding a JAR or a
Maven-style dependency.

And one can use the advanced development features of these IDEs to develop
@PaniniJ code, for example,

- Jump-to-definition.
- Contextual error messages (i.e. red-squigglies).
- Profiling.
- Interactive debugging.


### Want to Learn More?

Take a peek at [the tour](/docs/tour.html) to find out more about what you can
do with @PaniniJ.

Still interested? Start learning the syntax and running @PaniniJ programs with
[Chapter 2. Getting Started](/man/p1/ch2_getting_started.html) of
[The @PaniniJ Manual](/man/).
