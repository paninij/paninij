---
layout: home
title: "@PaniniJ"
---

#### Capsule-Oriented Programming in Java

@PaniniJ is a Java extension for safer and more modular concurrent
programming. It adds **capsules** to Java via an easy-to-integrate *compiler
plugin*.

<br/>


#### Concurrent Execution and Asynchronous Communication

Capsules are specified by writing annotated Java classes:

``` java
@Capsule class HelloWorldCore {
    public void run() {
        System.out.println("Hello, world.");
    }
}
```

Capsules run concurrently. They send messages to one another using method-call
syntax. These messages usually represent an asynchronous task that one capsule
wants another capsule to perform.

A *transparent future* to the result of this asynchronous task is returned
immediately. With this future, the client has the option to possibly

- Ignore the result.
- Wait for the result.
- Do some work, then wait for the result.
- Or just pass it on to another capsule.

The below example shows a client capsule asynchronously requesting two
potentially costly operations to be performed by two other capsule instances.

``` java
import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class MyClientCore {
    @Local Graph graphA;
    @Local Graph graphB;
    void run() {
        Path pathAB = graphA.shortestPath("A", "B");
        Path pathYZ = graphA.shortestPath("Y", "Z");
        // Do more work.
        // ...

        System.out.println(pathAB.toString());
        System.out.println(pathYZ.toString());
    }
}

@Capsule class GraphCore {
    // Encapsulated graph state.
    // ...
    public Path shortestPath(String vertexA, String vertexB) {
        // Sequential shortest path algorithm.
        // ...
    }
}
```

Notice that because these calls are asynchronous, the client doesn't need to
wait for the `pathAB` result before making another request to get `pathYZ`. It
is only later, where the `Path#toString()` methods are called that the client
will need to wait. Ideally, the client has been able to do some more work in
the mean time.

<br/>

#### Developed using Standard Java IDEs

@PaniniJ provides these (and many other) features by extending the Java compiler
to write Java code for capsules themselves. The capsules wrap the capsule cores
and manage their interactions in a thread-safe way.

There are a number of rules that statically restrict how a capsule can be
defined. Many of these rules are checked by our compiler plugin. If a check
fails, the error is reported just like an ordinary Java compiler error.

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

<br/>


#### Motivations

Existing concurrent programming practices leave practitioners with no choice but
to program using unbridled concurrency mechanisms, and then find and remove
concurrency errors.

We believe that this path is untenable. The Panini project investigates an
alternative: create abstractions that eliminate classes of concurrency errrors
by construction.

Our work focuses on an abstraction called a capsule, a boundary within which you
can write and reuse sequential code as-is. We work on increasing the class of
concurrency errors that can be eliminated from capsule-oriented programs by
construction.

<br/>


#### Goals

- **Solve pervasive and oblivious interference problems:** enable modular
  reasoning about concurrent programs.
- **Implicit concurrency:** eliminate usage of unsafe features like threads and
  locks.
- **Integrated compile-time analysis of concurrency hazards:** errors are caught
  early.
- **Retain familiarity:** Programmers need not switch to a completely new
  programming model.
- **Enable as-is reuse of sequential code:** portions of software are guaranteed
  to be single-threaded.
