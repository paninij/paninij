---
title: Preface
part: 1
chapter: 0
quote: An ounce of prevention is worth a pound of cure.
quote_source: De Legibus (c. 1240) by De Bracton (d.1268)
---

It's a pity that engineering correct and efficient concurrent software remains a
daunting task in this second decade of the 21st century. Programming language
support for engineering concurrency-safe programs by construction in modern
mainstream languages is, at best, inadequate. External tools may help verify
concurrency safety properties of a codebase. However, this requires an
additional step in the toolchain, and more importantly, these tools have had
limited applicability in modern mainstream languages because of the unbridled
power of these language's concurrency primitives.

We believe that cutting-edge development of programming languages is too
fequently done in isolation from tool support. We believe that this missed
opportunity ultimately hurts the productivity of software engineers.

The @PaniniJ project is an attempt to extend the Java language to improve its
concurrency model and to provide better compile-time checking of concurrency
safety properties. In particular, our goals are as follows:

1. **Simplify Concurrency Features.** While interesting patterns of concurrent
   program design are of significant intellectual interest to select few, we
   believe that a few simple constructs suffice for most programmers' needs.

2. **Simplify Concurrent Interactions.** The behaviors of a concurrent entity
   should be designed such that its control flow is "constrained" in a clear
   way. (Think "Structured Programming".) This is a first step to greatly help
   the programmer reason about and visualize the interactions between concurrent
   components. This, in turn, makes it easier to make and modify design
   decisions about concurrency and synchronization within a system.

3. **Make Concurrent Programs Safe By Construction.** To get safer concurrent
   programs, we should provide strong safety guarantees by construction. The
   preconditions for these safety guarantees should--whenever possible--be
   checked automatically via built-in checks (ideally at compile time, but when
   necessary at runtime).

4. **Limit the Complexity of Reasoning.** To simplify the process to reason
   about and understand concurrent programs, we should significantly decrease
   the number of program locations involved in reasoning about a concurrent
   task.

To satisfy these goals, the @PaniniJ compiler plugin adds to Java a programming
model called capsule-oriented programming, where programmers describe the
concurrent aspects of their system in terms of its modular structure and then
write sequential code to implement the operations of those modules using a new
abstraction that we call a *capsule*. A key aspect of this approach is that
capsule-oriented programs look like familiar sequential programs but they are
implicitly concurrent.

This programming guide describes @PaniniJ, further motivations behind the
project, its main constructs and features, some common programming patterns in
the capsule oriented style, and finally building, installing, and running
@PaniniJ programs.

If you are completely new to @PaniniJ, you may want to read our getting started
guide in [Chapter 3](/man/p1/ch2_getting_started.html) or to look at one of our
examples in [Part 3](/man/p3/).
