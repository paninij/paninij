---
layout: home
title: "@PaniniJ"
---

#### What is it?

@PaniniJ is a Java extension for safer and more modular concurrent
programming. It adds **capsules** to Java via an easy-to-integrate compiler
plugin.

Capsules are a new concurrent programming abstraction. They are like Actors, but
with certain static guarantees that are checked at compile-time. If one of these
static checks fails, the error is reported just like an ordinary Java compiler
error.

Adding @PaniniJ to your Java project is as simple as adding a JAR or a
Maven-style dependency.


#### Why?

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
