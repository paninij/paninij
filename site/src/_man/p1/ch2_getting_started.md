---
part: 1
chapter: 2
title: Getting Started
---

## Panini’s Goals

A central goal of capsule-oriented programming and the Panini language is to
help programmers deal with the challenges of concurrent program design.

The value proposition of the programming paradigm and the compiler plugin is to
enable greater program modularity and, in doing so, to automatically enable
greater program concurrency. In fact, @PaniniJ does not use explicit concurrency
features. Instead, the programmer modularizes a program using capsules, which
implicitly specify boundaries outside of which concurrency can occur. The
@PaniniJ runtime will automatically enable concurrency in between the boundaries
of capsules when it is safe to do so.


## Hello World!

An @PaniniJ program is a collection of one or more capsules. A simple
"Hello World" capsule can be written as follows.

**Listing 2.1:** Hello World in @PaniniJ

``` java
package hello;

import org.paninij.lang.Capsule;
import org.paninij.lang.Root;

@Root @Capsule
class HelloWorldCore {
  void run(){
    System.out.println("Panini: Hello World!");
    long time = System.currentTimeMillis();
    System.out.println("Time is now: " + time);
  }
}
```
{: .code-with-line-numbers}

Notice that `HelloWorldCore` is just a plain old Java class. Any standard Java
compiler will accept and compile this code. However, when the @PaniniJ compiler
plugin is added to a Java compiler, the annotations on line 6 give this class
*extra meaning* to the compiler. Because of these annotations, extra compiler
checks and extra code generation will be performed by the compiler.

In particular, because of the `@Capsule` annotation, this class is interpreted
by our @PaniniJ-supported Java compiler as a *capsule declaration*: it declares
a capsule named `HelloWorld`.

Also, the `run()` method, spanning lines 9-13, doesn't just declare and define a
method. `run()` is also interpreted as the `HelloWorld` capsule's run
declaration. This run declaration is the code which a `HelloWorld` capsule
instance will run once that capsule is started. A run declaration just contains
normal Java expressions and statements. In particular, this run declaration says
that this capsule will just print the hello world message, print the current
time, and then terminate.

An @PaniniJ capsule such as `HelloWorld` can only be started and executed as
part of *a capsule system*. On lines 6-8 of the Listing 2.2 below, we see a
normal Java `main()` method which calls `CapsuleSystem#start()`. This is one
way to start a capsule system.

**Listing 2.2:** Starting a capsule system with `HelloWorld` as its root.

``` java
package hello;

import org.paninij.lang.CapsuleSystem;

class Main {
  public static void main(String[] args) {
    CapsuleSystem.start(HelloWorld.class, args);
  }
}
```
{: .code-with-line-numbers}

The `Main` class, the `HelloWorldCore` class, and the `HelloWorld` capsule
together form a complete @PaniniJ program that can be compiled and executed.
When this program is executed, it appears to just run the body of the run
declaration.


## Compiling and Running Hello World

There are various ways set up your Java development environment to compile and
run this program. You can use your favorite Java IDE (e.g. Eclipse, IntelliJ
IDEA) and/or your favorite build tool (e.g. Maven, Gradle). (See [Chapter
3](/man/p4/ch3_development_environment.html) for more information.)

But at a minimum, you need a standard Java 8 compiler, two @PaniniJ JARs, and
the JVM. The latest @PaniniJ JARs can be downloaded from the
[@PaniniJ GitHub releases page](https://github.com/paninij/paninij/releases).

Once you have downloaded the @Panini JARs, open your favorite text editor along
side of them, then save Listing 2.1 to a file `hello/HelloWorldCore.java` and
save Listing 2.2 to a file `hello/Main.java`. Your directory hierarchy should
look like this:

```
TODO
```

**TODO:** Add `javac` and `java` instructions here.

The printed time is the difference, measured in milliseconds, between the time
at which this command was issued and midnight, January 1, 1970 UTC.

As per usual with a hello world example, this behavior isn't terribly
interesting. This is because this capsule system just contains a single capsule
which runs to completion. We aren't yet seeing capsules run concurrently and
interacting with one another. The next section demonstrates how to specify a
graph of concurrently running capsules and to make one capsule invoke the
procedures of another.


## Decomposing a Program into Capsules

A capsule-oriented program can have more than one capsules. To illustrate, let
us decompose our hello world program from previous section into two parts.
Throughout this book we will use David Parnas’s information hiding principle as
our guide for program design. In essence, this principle says that one should
decompose a program into parts in a manner such that each part is designed to
“know about” and “hide” certain key decisions about how that program is
implemented. This is done so that, if necessary, those decisions can be changed
later by us and others.

We can decompose our HelloWorld program into three parts: a Greeter capsule that
knows about the method of proper greeting, e.g. “Hello” in English, “Namaste” in
Hindi, a Console capsule that knows about the medium that will be used to convey
the greeting, e.g. standard output, a file, and a HelloWorld capsule that puts
these parts together.

**Listing 2.3:** Hello World Decomposed!

``` java
@Capsule
class ConsoleCore {
  void write(String s) {
    System.out.println(s);
  }
}

@Capsule
class GreeterCore {
  @Imported Console c;
  void greet() {
    c.write("Panini: Hello World, Decomposed!");
    long time = System.currentTimeMillis();
    c.write("Time is now: " + time);
  }
}

@Root @Capsule
class HelloWorldCore {
  @Local Console c;
  @Local Greeter g;
  void design() {
    g.imports(c);
  }
  void run() {
    g.greet();
  }
}
```
{: .code-with-line-numbers}

This new version declares a capsule Console on line 1. This capsule declares a
single procedure write on line 2 that writes its argument s on the standard
output.

The Greeter capsule definition now contains a specification of what other
capsules it requires. On line 7, it says that a Greeter requires a Console to
work properly.

Once a capsule definition declares such requirements, procedures of the required
capsules can be called. For example, the write procedure of the Console capsule
is called on lines 9 and 11 in the Greeter capsule.

As you might notice the Console and the the Greeter capsules do not have a run
procedure, but the HelloWorld does. The run procedure is optional and signals
that the capsule can start computation without external stimuli (i.e. if said
capsule is instantiated in a program, then the run procedure of that capsule
will be executed as soon as program initialization finishes).

On lines 16-20, this program has a design declaration, a new feature in Panini.
The role of a design declaration is to define parts of a Panini program
(typically capsules) and how these parts are connected. The design declaration
on lines 16-20 simply states that this capsule HelloWorld will, as its internal
parts, contain one instance of capsule Console (line 17), one instance of
capsule Greeter (line 18). On line 19, the design declaration states that the
Greeter instance g is connected to the Console instance c.


## Implicit Concurrency in Capsule-oriented Programs

As mentioned previously, Panini does not use explicit concurrency features.
Instead, the programmer modularizes a program using capsules, which implicitly
specify boundaries outside of which concurrency can occur. The Panini runtime
will automatically enable concurrency in between the boundaries of capsules when
safe to do so.

When a procedure is called on an external capsule, e.g. call to write procedure
on line 9, and if it is safe to do so, the call immediately returns allowing the
caller capsule and the callee capsule to work independently. Here, the Greeter
capsule can immediately continue to obtain the current system time, while the
Console capsule prints first line of the greeting.

This is the main benefit of capsule-oriented programming and the Panini
language. Implicit concurrency is achieved without having to introduce explicit
concurrency features like threads, task pools, etc. This simplifies programming
tasks. Not having to worry about concurrency is the main promise of
capsule-oriented programming.

Now that you’ve written your first Panini program it is time to familiarize
yourself with more complex features of the language in the next chapters.
