---
title: Overview
part: 1
chapter: 1
quote: An ounce of prevention is worth a pound of cure.
quote_source: De Legibus (c. 1240) by De Bracton (d.1268)
---

Its a pity that engineering correct and efficient concurrent software remains a
daunting task in this second decade of the 21st century. Programming language
support for engineering concurrency-safe programs by construction in modern
mainstream languages is inadequate at best. Even if we are to ignore the fact
that deploying external tool support for verifying concurrency safety properties
requires an additional step in the toolchain, such tools have had limited
applicability due to the unbridled concurrency power of modern mainstream
languages.

Prevailing dynamics has been that the research on and development of programming
languages and that of tool support are in non-cooperative roles, which
ultimately hurts productivity of software engineers.

The Panini programming language is an attempt to design concerted support for
concurrency in both programming models and compile-time checking. Specifically,
the project has four goals:

1. Simplifying concurrency support, while interesting patterns of concurrent
program design are of significant intellectual interest to select few, we
believe that simple constructs suffice for most needs,

2. ease the process of visualizing the interactions between the components, in
order to make design decisions about concurrency and synchronization,

3. ease the process of producing safer concurrent programs by construction by
providing automated, in-built support for error checking, and

4. ease the process of reasoning and understanding concurrent programs by
significantly decreasing the number of program locations that necessitate
reasoning about concurrent tasks.

To satisfy these goals, the Panini language proposes a programming model called
capsule-oriented programming, where programmers describe a system in terms of
its modular structure and write sequential code to implement the operations of
those modules using a new abstraction that we call capsule. Capsule-oriented
programs look like familiar sequential programs but they are implicitly
concurrent. This programming guide describes Panini, the motivation behind the
language in more detail, its main constructs and features, common programming
patterns in the programming language, and finally installing and running Panini
programs.

If you are completely new to the Panini programming language, you may want to
read our guide on how to get started in [Chapter 3]() or look at one of our
examples in [Part 3](/manpages/part3/).
