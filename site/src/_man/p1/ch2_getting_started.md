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

It may seem strange to use `HelloWorld.class` even though we haven't defined
such a class yet. This is okay, because we are relying on the @PaniniJ compiler
plugin to inspect `HelloWorld` capsule declaration (i.e. the `HelloWorldCore`
class) and to then create `HelloWorld` for us. This distinction between a
user-written capsule core and the @PaniniJ-generated capsule is very important.
We will discuss why later in this manual.

The `Main` class, the `HelloWorldCore` class, and the `HelloWorld` capsule
together form a complete @PaniniJ program that can be compiled and executed.
When this program is executed, it appears to just run the body of the run
declaration.


## Compiling and Running Hello World

There are various ways set up your Java development environment to compile and
run this program. You can use your favorite Java IDE (e.g. Eclipse, IntelliJ
IDEA) and/or your favorite build tool (e.g. Maven, Gradle). We describe these
various configurations in [Chapter 3](/man/p1/ch3_development_environment.html),
but at a minimum, all you need is a standard Java 8 compiler, two @PaniniJ JARs,
and the JVM. The latest @PaniniJ JARs can be downloaded from the
[@PaniniJ GitHub releases page](https://github.com/paninij/paninij/releases).

Once you have downloaded the @Panini JARs, open your favorite text editor along
side of them, then save Listing 2.1 to a file `hello/HelloWorldCore.java` and
save Listing 2.2 to a file `hello/Main.java`. Your directory hierarchy should
look like this:

```
$ tree
.
├── hello
│   ├── HelloWorldCore.java
│   └── Main.java
├── org.paninij-lang-0.2.0.jar
└── org.paninij-proc-0.2.0.jar

1 directory, 4 files
```

Now, to compile and execute the program, just run the following commands.

```
$ javac -cp org.paninij-lang-0.2.0.jar:org.paninij-proc-0.2.0.jar hello/*.java
$ java -cp .:org.paninij-lang-0.2.0.jar hello.Main
Panini: Hello World!
Time is now: 1487326874036
```

The first command says to compile both of the java files under `hello/`,
searching in the @PaniniJ `lang` and `proc` JARs for classes. The second command
says to run the JVM, searching for classes in the @PaniniJ JARs and also the
files we just built and using as our entry point the `hello.Main` class. The
last two lines are STDOUT from the program. (The time is the number of
milliseconds since [the UNIX epoch](https://en.wikipedia.org/wiki/Unix_time).)

The curious may be interested to take a look at the files created by our
compilation.

```
$ tree
.
├── hello
│   ├── HelloWorld$Monitor.class
│   ├── HelloWorld$Monitor.java
│   ├── HelloWorld$Serial.class
│   ├── HelloWorld$Serial.java
│   ├── HelloWorld$Task.class
│   ├── HelloWorld$Task.java
│   ├── HelloWorld$Thread.class
│   ├── HelloWorld$Thread.java
│   ├── HelloWorld.class
│   ├── HelloWorld.java
│   ├── HelloWorldCore.class
│   ├── HelloWorldCore.java
│   ├── Main.class
│   └── Main.java
├── org.paninij-lang-0.2.0.jar
└── org.paninij-proc-0.2.0.jar

1 directory, 16 files
```

Notice that the compiler didn't just create class files, because of the @PaniniJ
compiler plugin, it also created new Java source files (e.g.
`hello/HelloWorld.java`, `hello/HelloWorld$Thread.java`, etc.)

Notice how easy it was to use the @PaniniJ compiler plugin. We didn't need to
change or configure the Java compiler at all. Just by including these JARs on
the compiler classpath, `javac` discovers and uses the @PaniniJ compiler plugin.

As is usual with a hello world example, this one is meant to give a small sense
of @PaniniJ's syntax and tooling. But in itself, the behavior of this program
isn't terribly interesting, because our capsule system just contains a single
capsule which runs to completion. We aren't yet seeing capsules run concurrently
and interacting with one another. The next section demonstrates how to specify a
graph of concurrently running capsules and how to make one capsule invoke the
procedures of another capsule.


## Decomposing a Program into Capsules

A capsule-oriented program can have more than one capsule. To illustrate, let
us decompose our hello world program from the previous section into two parts.

Throughout this book we will use David Parnas’s information hiding principle as
our guide for program design. In essence, this principle says that one should
decompose a program into parts in a manner such that each part is designed to
"know about" and "hide" certain key decisions about how that program is
implemented. This is done so that, if necessary, those decisions can be changed
later by us and others without being forced to change too many aspects of the
program.

We can decompose our previous `HelloWorld` program into three parts:

- a `Console` capsule that knows about the medium that will be used to convey
  the greeting (e.g. standard output, a file),
- a `Greeter` capsule that knows about the method of proper greeting (e.g.
  "Hello" in English, "Namaste" in Hindi), and
- a `HelloWorld` capsule that puts these parts together.

**Listing 2.3:** *Hello World Decomposed!*

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

(**Note:** For brevity, this listing elides a few details of this program, in
particular, separation into distinct files, package declarations, import
statements, and an invocation of `CapsuleSystem#start()` on a root capsule.
These details are similar to what we saw in our last example and to what we
would expect from our experience with Java. Subsequent listings in this manual
may also elide some of these details.)

We will explain this program's syntax and semantic below, but a basic outline of
the program is that:

- Lines 1-6 declare a `Console` capsule. This capsule declares a single
  procedure, `write()`, that writes its argument `s` to STDOUT.

- Lines 8-16 declare a `Greeter` capsule. A `Greeter` includes a single
  procedure, `greet()` that uses a `Console` capsule.

- Lines 18-28 declare a `HelloWorld` capsule. The `@Root` indicates that this
  capsule can be used as the root of an executable capsule system.

Notice that the `Console` and `Greeter` capsules do not have a run declaration,
but the `HelloWorld` does. A run procedure is optional and signals that the
capsule will execute without any external stimuli. Thus, in this program, the
`HelloWorld` capsule is responsible for driving the other capsules, and the
other capsules are responsible for asynchronously and concurrently reacting to
its requests.

If a capsule with a run declaration is instantiated in a program, then that
capsule will execute the body of the declaration and then terminate.
{: .lead}

A run declaration is often used to to initially trigger the program. Subsequent
activity within the program is often a reaction to tasks triggered by some run
declaration.

Previously we saw that our `@Capsule` annotation on a capsule core class meant
that a `run()` method was interpreted by @PaniniJ as a run declaration. We see
something similar here where the structure of the core class is interpreted by
@PaniniJ to mean more than in ordinary Java. In particular, certain methods are
interpreted as *procedure declarations* and certain fields are interpreted as
*capsule references*.


### Procedure Declarations

Some capsule core methods are interpreted as procedure declarations.

If a method of a capsule core is neither an @PaniniJ-specific declaration (e.g.
a run declaration) nor `private`, then that method is interpreted as a
*procedure declaration* of that capsule.
{: .lead}

There are two procedure declarations in this program:

- Lines 3-5 declare the `write` procedure to be a part of the `Console` capsule.
- Lines 11-15 declare the `greet` procedure as part of the `Greeter` capsule.

Procedures are how one capsule interacts with another. We say that a procedure
is *invoked* to distinguish between two distinct concepts that looks
syntactically identical:

- A Java class method is *called*. This happens *synchronously*, that is, the
  calling context waits until the called context completes its execution.

- An @PaniniJ capsule procedure is *invoked*. This happens (potentially)
  *asynchronously*, that is, the invoking capsule can send a request for some
  task to be performed and then continue without waiting for the invoked capsule
  to complete this request.

A procedure should only be invoked from within a capsule declaration, not by
any ordinary Java code. Capsules can both call methods and invoke procedures.


### Capsule Reference Declarations

Some capsule core fields are interpreted as capsule references.

If a field of a capsule core has a type which is capsule, then that field is
interpreted as a capsule reference declaration.
{: .lead}

There are three capsule reference declarations in this program:

- Line 10 says that a `Greeter` capsule always has a reference to a `Console`
  capsule.
- Line 20 says that a `HelloWorld` capsule always has a reference to a `Greeter`
  capsule.
- Line 21 is similar to line 20 but with respect to a `Greeter` capsule.

Each declares the existence of some other capsule on which this capsule depends.
Essentially, these capsule reference declarations are specifying all of the
other capsules which this capsule *directly* depends.

It is only via these capsule references that one capsule can invoke a procedure
on another capsule, and it is only via procedure invocations that one capsule
can directly use another capsule. For example, we can see `Greeter` use the
`Console` capsule on lines 12 and 14 when it invokes the `write` procedure of
a `Console` capsule instance.

Notice that nowhere in this program have we initialized these fields. This is
because the @PaniniJ runtime does this for us. (These fields should be
considered `final`.) The @PaniniJ runtime performs this initialization in one of
two different ways:

- If the field is annotated with `@Local`, then a new capsule will automatically
  be created.
- If the field is annotated with `@Imported`, then an existing capsule will need
  to be provided from elsewhere.

Because of line 10, `Greeter` needs to be provided with some `Console` in order
for the `Greeter` to work. In this example, this is done using the design
declaration on lines 22-24.

Design declarations are used to connect capsule instances to one another.
Specifically, if a capsule declares an `@Local` capsule reference to some
capsule which has some `@Imported` fields, then the design declaration is used
to say what should be imported.

We see an example of this on line 23. The `Greeter` capsule `g` (whose existence
is declared on line 21) can't function without importing some `Console` (see
line 10). So, it is the responsibility of the `HelloWorld` capsule to provide
an import. How this is provided is specified on line 23, which says that the
capsule instance denoted by `c` should also be available to the capsule instance
denoted by `g`.



## Implicit Concurrency in Capsule-oriented Programs

As mentioned previously, Panini does not use explicit concurrency features.
Instead, the programmer modularizes a program using capsules. The structure of
a capsule implicitly specifies boundaries outside of which concurrency can
occur, but inside of which execution is sequential. The Panini runtime will
automatically enable concurrency in between the boundaries of capsules when it
safe to do so.

When a procedure is invoked on a capsule, the procedure invocation is completed
immediately, even if the invoked procedure hasn't even started executing our
procedure invocation. This allows the invoking capsule and the invoked capsule
to work independently and simultaneously.

This program includes a very simple example of this: the Greeter capsule can
invoke `write()`, but then immediately immediately continue to obtain the
current system time, even before the `Console` capsule prints first line of the
greeting.

This is the main benefit of capsule-oriented programming and the Panini
language. Implicit concurrency is achieved without having to introduce explicit
concurrency features like threads, task pools, etc. This simplifies programming
tasks. Being able to exploit concurrency without having to worry about many of
its hazards is the main promise of capsule-oriented programming.

Now that you’ve written your first @PaniniJ program, it is time to familiarize
yourself with more complex features of the language in the next chapters.
