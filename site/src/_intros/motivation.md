---
title: Motivation
short_title: Motivation
---

Existing concurrent programming practices leave practitioners with no choice but
to program using unbridled concurrency mechanisms, and then find and remove
concurrency errors. We, Panini project team members, believe that this path is
untenable. The Panini project investigates an alternative: create programming
abstractions that eliminate classes of concurrency errrors by construction. Our
work focusses on an abstraction called a capsule, a boundary within which you
can write and reuse sequential code as is. The research portion of the Panini
project team works on increasing the class of concurrency errors that can be
eliminated from capsule-oriented programs by construction.

PaniniJ is an implementation of the Panini programming model that enables the
use of standard Java tools for capsule-oriented programming. The Panini model is
a set of rules that eliminates certain classes of errors that are common in
explicit concurrent programs.

PaniniJ utilizes annotation processing, a standard method of hooking into the
Java compiler, to generate concurrent code based on templates created by the
user. By converting the keywords of the PaniniJ language into annotations, we
are able to enforce the Panini model while still allowing users to utilize any
java tools they want. This Manual is for getting started with PaniniJ in the
Eclipse IDE.
