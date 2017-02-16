---
part: 1
chapter: 2
title: Getting Started
---

## Panini’s Goals

A central goal of capsule-oriented programming and the Panini language is to
help programmers deal with the challenges of concurrent program design.

The value proposition of the programming paradigm and the programming language
is to enable greater program modularity and in doing so automatically enable
greater program concurrency. In fact, Panini does not use explicit concurrency
features. Instead, the programmer modularizes a program using capsules, which
implicitly specify boundaries outside of which concurrency can occur. The Panini
runtime will automatically enable concurrency in between the boundaries of
capsules when safe to do so.

## Hello World!

A Panini program is a collection of zero or more capsules. For example, a simple
"hello world" program in Panini can be written as follows:

Listing 3.1: Hello World in Panini

``` java
capsule HelloWorld {
  void run(){
    System.out.println("Panini: Hello World!");
    long time = System.currentTimeMillis();
    System.out.println("Time is now: " + time);
  }
}
```

This program declares a capsule called HelloWorld. The declaration of this
capsule starts on line 1 and ends on line 7. The capsule HelloWorld contains
only one procedure, run on line 2, which prints a message “Panini: Hello World!”
on line 3 and prints current time on line 5.

This is a complete Panini program that can be compiled and executed.

When this program is executed, since it has only one capsule, and that capsule
has a procedure named run, code inside that procedure is executed.


## Compiling and running Hello World!

To compile and run this program, you will need the Panini compiler panc and the
Panini executable panini. Both these applications are available from the Panini
web-page http://paninij.org for download. For more information about installing
and running the compiler see chapter 13.

Once you have downloaded and installed the Panini distribution, open your
favorite text editor and save the HelloWorld program in listing 3.1 in a new
file HelloWorld.java.

To compile this program simply run:

```
$ panc HelloWorld.java
You can then run this panini program with:
$ panini HelloWorld
Panini: Hello World!
Time is now: 1375940448626
```

The printed time is the difference, measured in milliseconds, between the time
at which this command was issued and midnight, January 1, 1970 UTC.


## Decomposing a Program into Capsules

A capsule-oriented program can have more than one capsules. To illustrate, let
us decompose our HelloWorld program from previous section into two parts.
Throughout this book we will use David Parnas’s information hiding principle as
our guide for program design. In essence, this principle says that one should
decompose a program into parts in a manner such that each part is designed to
“know about” and “hide” certain key decisions about how that program is
implemented. This is done so that, if necessary, those decisions can be changed
later by us and others.1

We can decompose our HelloWorld program into three parts: a Greeter capsule that
knows about the method of proper greeting, e.g. “Hello” in English, “Namaste” in
Hindi, a Console capsule that knows about the medium that will be used to convey
the greeting, e.g. standard output, a file, and a HelloWorld capsule that puts
these parts together.

**Listing 3.2:** Hello World Decomposed!

``` java
capsule Console {
  void write(String s) {
   System.out.println(s);
  }
}

capsule Greeter ( Console c ) {
  void greet() {
    c.write("Panini:␣Hello␣World,␣Decomposed!");
    long time = System.currentTimeMillis();
    c.write("Time␣is␣now:␣" + time);
  }
}

capsule HelloWorld {
 design {
  Console c;
  Greeter g;
  g(c);
 }
 void run() {
  g.greet();
 }
}
```

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
