---
title: Some Frequently Asked Questions about PaniniJ
short_title: FAQ
permalink: /docs/faq.html
---

* TOC
{:toc}

##### What is Capsule-oriented Programming?

Capsule-oriented programming is a programming paradigm that aims to ease
development of concurrent software systems by allowing abstraction from
concurrency-related concerns.

Capsule-oriented programming entails breaking down program logic into distinct
parts called capsule declarations and composing these parts to form the complete
program using system declaration.


##### Who can benefit from capsule-oriented programming?

Capsule-oriented programming is a suitable approach for programmers who don't
want to be distracted by concurrency concerns so that they can focus on their
software's logic. If you want your programming language to take care of your
concurrency concerns just like Java and C# handles your memory management
concerns then capsules-oriented programming is for you. Who may NOT benefit from
capsule-oriented programming?

Capsule-oriented programming may not be a suitable approach for a software
project if it is preferred in that project to manage every aspect of concurrency
manually. This is similar to why Java and C# are not suitable, if a software
project requires manual memory management and explicit pointer arithmetic.


##### What do I need to know about concurrency in order to learn the Panini language?

Nothing.


##### Will I be able to use my existing object-oriented code when I move my project to the Panini language?

Mostly. All existing classes that do not have a main method would work as is. It
is also advisable to avoid using explicit concurrency features, e.g. threads,
locks, synchronized, etc since they may interfere with Panini's internal
mechanisms.


##### How are capsules different from Erlang actors?

Capsules are different from Erlang actors in that capsules ensure messages arrive in sequentially consistent order, as well as providing mutable state within the actor.


##### How are capsules different from Scala actors?

Capsules are different from Scala actors because of the enforced confinement of
a capsule's state.

##### Why are capsules different from formal definition of actors in Hewitt, Bishop, and Steiger's original 1973 paper? Is it just a case of "Not Invented Here" (NIH) syndrome?

The decision to make different choices in capsule's design was based on two
factors:

- To decrease the impedance mismatch between mainstream languages like Java, C#,
  etc and the capsule-oriented programming model. In the resulting design,
  inter-capsule procedure calls look like ordinary method calls in mainstream
  programming languages.
- We also simplified the language model a bit to make it feasible to build
  efficient, precise, and more automated analysis and compilation strategies.

Here is a precise comparison:

<div class="row">
<div class="col-md-8 offset-md-2">
<img src="/img/actor_difference.png"
     class="img-fluid"
     alt="Comparison between Actors and Capsules">
</div>
</div>
<br />



##### How are capsules different from standard object-oriented classes?

A capsule is like a class in that it also defines a set of public operations,
hides the implementation details, and could serve as a work assignment for a
developer or a team of developers. Beyond these standard responsibilities, a
capsule also serves as a memory region for some set of standard object instances
and behaves as an independent logical process.


##### Why did you introduce new syntax for capsules and system instead of a library or annotation-based syntax?

At first glance a capsule declaration may look similar to a class declaration,
thus naturally raising the question as to why a new syntactic category is
essential, and why class declarations may not be enhanced with the additional
capabilities that capsules provide, namely, confinement (as in Erlang) and an
activity thread (as in previous work on concurrent OO languages). There are
three main reasons for this design decision in Panini.

First, we believe based on previous experiences that objects may be too
fine-grained to think of each one as a potentially independent activity. Second,
we wanted to specify a system as a set of related capsules with a fixed
topology, in order to make it feasible to perform static analysis of the system
graphs; this implies that capsules should not be first-class values. Third,
there is a large body of OO code that is written without any regard to
confinement. Changing the semantics of classes would have made reusing this vast
set of libraries difficult, if not impossible. In the current design of Panini,
since syntactic categories are different, sequential OO code can be reused
within the boundary of a capsule without needing any modification.


##### I believe I have just run into a compiler bug, how do I report it?

All bugs in the Panini compiler can be reported by sending an e-mail to
`panini@iastate.edu`. We would very much appreciate a self-contained source code
file that reproduces the bug along with your e-mail, but if you don't have it
you could also describe what you were trying to do and send us buggy output of
the Panini compiler.


##### Why do my files have the extension .java if I am writing Panini code?

The Panini compiler was built on top of the Java OpenJDK compiler and,
currently, we re-use most of the infrastructure provided by it. Panini files
will have their own extension in future releases of the compiler.


##### Can I compile all Java code with the Panini compiler?

All code that does not have use any explicit concurrency features (e.g.
synchronized keyword, threads, etc.) can be compiled using the Panini compiler.
Also, the Java main method no longer has any special meaning in the Panini
language.


##### I compiled the HelloWorld Panini program and now I have all these `.class` files, can some of them be removed?

Assuming you want your program to run, no. Currently, Panini programs first
compile to Java programs and then to bytecode, the extra files contain
everything needed to express Panini's abstractions in Java.
