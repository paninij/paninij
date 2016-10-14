---
title: About the Panini Project
short_title: About
---

#### Capsule-oriented programming

Capsule-oriented programming is a new programming style designed to address the
challenges of concurrent programming. Main goal is to enable non-concurrency
experts to write correct and efficient concurrent programs. If you are
unfamiliar, the Panini programming guide explains the challenges of writing
correct and efficient concurrent programs.

A central goal of this programming style is to provide tools to enable
programmers to simply do what they do best, that is, to describe a system in
terms of its modular structure and write sequential code to implement the
operations of those modules. To achieve this, capsule-oriented programming
introduces a new abstraction called capsule. A capsule is similar to a process;
it defines a set of public operations, and also serves as a memory region for
some set of ordinary objects.

One goal in capsule-oriented programming is that the programmer should get the
benefits of asynchronous execution without being forced to adapt to an
asynchronous, message-passing style of programming. To the programmer,
inter-capsule calls look like ordinary method calls. Capsule-oriented programs
are implicitly concurrent. There are no explicit threads or synchronization
locks; if necessary or beneficial, concurrency is introduced by the compiler.
Capsule-oriented programming eliminates two classes of concurrency errors:
sequential inconsistency and race conditions due to shared data.

#### Audience

**Who can benefit?** Capsule-oriented programming is a suitable approach for
programmers who don't want to be distracted by concurrency concerns so that they
can focus on their software's logic. If you want your programming language to
take care of your concurrency concerns just like Java and C# handles your memory
management concerns then capsules-oriented programming is for you.

**Who may NOT benefit?** Capsule-oriented programming may not be a suitable
approach for a software project if it is preferred in that project to manage
every aspect of concurrency manually. This is similar to why Java and C# are not
suitable, if a software project requires manual memory management and explicit
pointer arithmetic.

#### The Panini language

Panini is a capsule-oriented programming language whose goals are to ease
development of correct, scalable, and portable concurrent software. See below
for origin of the name. PaniniJ, in particular, is a capsule-oriented extension
of the Java programming language that runs on the standard JVM platform. Panini
provides new, implicitly concurrent, modularization mechanisms. If programmers
use these mechanisms to structure their software system to improve modularity in
its design, they get implicit concurrency at the boundaries of these mechanisms.
Modularization leads to improved concurrency. The language thus encompasses
fundamental and practical efforts to unify modularization and parallelization
mechanisms.

#### Design goals

Panini has the following main design goals:

- Improve modularity, while exposing implicit concurrency in software design
  leading to improved utilization of emerging multicore and manycore platforms.

- Support sequential consistency and data race freedom in the presence of
  implicit concurrency.

- Enable separate type-checking, compilation, and modular reasoning of both
  sequential and concurrent code.

All these goals are important for building correct, scalable, and portable
software systems in the multicore/manycore/cloud era but harder to achieve in
current programming languages.

#### Origin of name

The language takes its name from Panini (fl.c.400 BC), an Indian grammarian,
known for his formulation of the Sanskrit grammar rules (the earliest work on
linguistics). If you are completely new to the idea and the Panini project, you
may want to read our overview.
