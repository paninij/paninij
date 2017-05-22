---
title: A Tour of @PaniniJ Features
short_title: Tour
permalink: /docs/tour.html
---

Panini is a programming model for describing modular concurrent computation
using the concept of a [*capsule*](http://dl.acm.org/citation.cfm?id=2819117). 
A capsule is a concurrent entity similar to an
[actor](https://en.wikipedia.org/wiki/Actor_model).
Main difference between a capsule and an actor is manifest typing. 
A capsule explicitly identifies the type of other capsules that it interacts with,
and the type of messages that it can receive.

@PaniniJ takes the idea of a capsule and brings it to any programmer familiar
with Java and working on the JVM. Code created with @PaniniJ is safer and
easier to understand than code relying on traditional Java threads.

An @PaniniJ capsule is specified by writing a Java class. By just adding the
`@Capsule` annotation to a Java class, that class can be interpreted as a
capsule.

``` java
@Capsule
class HelloWorldCore {
    void run() {
        System.out.println("Hello, World.");
    }
}
```
{: .code-with-line-numbers}

We call this class *the capsule's core*. It is how one can specify

- the state encapsulated by the capsule
- the other capsules which this capsule knows about
- code for handling requests from other capsules

You can think of a capsule core as the stateful and behavioral center of a
capsule. The @PaniniJ runtime is responsible for wrapping and executing a graph
of these capsule cores, all running in parallel.

In @PaniniJ, these capsules communicate with one another by what looks like a
normal Java method calls. However, a call actually triggers *an asynchronous
message send*. To distinguish these inter-capsule calls from normal Java method
calls, we say that one capsule *invokes a procedure* on another capsule.

Here is an example where a `HelloWorld` capsule invokes the `work()` procedure
on a `Worker` instance:

``` java
@Capsule
class HelloWorldCore {
    // Declare the existence of another capsule.
    @Local Worker w;

    void run() {
        // An async procedure invocation.
        Bar b = w.work(new Foo(...));
        // Do our own work.
        // ...
        // Finally, use the result.
        System.out.println(b);
    }
}

@Capsule class WorkerCore {
    // A procedure. It can be invoked asynchronously by other capsules.
    Bar work(Foo f) {
        Bar b;
        // Do lots of work.
        // ...
        // Eventually return the result of the work.
        return b;
    }
}
```
{: .code-with-line-numbers}

Additionally, what appears to be an inter-capsule return values is
actually an implicit future to the request's result. The requesting capsule may
block on this future immediately, or it may do some other useful work before
blocking, or it may just ignore it.

Defining a graph of capsules is easy, because a communication link and/or node
in the graph is just specified by declaring a capsule core field with an
annotation.

These and other features can be added to a Java project by including @PaniniJ as
just another Java project dependency (e.g. a JAR or a Maven dependency).
Specifically, you need to depend on the @PaniniJ annotation processor. An
annotation processor extends any standards-compliant java compiler (e.g.
Oracle's `javac`, Eclipse, IntelliJ IDEA, etc).


### Want to Learn More?

Want to start compiling and running @PaniniJ programs?
[Chapter 2. Getting Started](/man/p1/ch2_getting_started.html) of
[The @PaniniJ Manual](/man/).
