---
layout: page
title: About PaniniJ
permalink: /about/
---

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

PaniniJ is an implementation of the Panini programming model that enables the
use of standard Java tools for capsule-oriented programming. The Panini model is
a set of rules that eliminates certain classes of errors that are common in
explicit concurrent programs.

PaniniJ utilizes annotation processing, a standard method of hooking into the
Java compiler, to generate concurrent code based on templates created by the
user. By converting the keywords of the PaniniJ language into annotations, we
are able to enforce the Panini model while still allowing users to utilize any
java tools they want.

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


#### History

- [The ISU Laboratory for Software Design](http://design.cs.iastate.edu)
- [Research](http://design.cs.iastate.edu/projects.html#panini)
- [Old Panini Home Page](http://web.cs.iastate.edu/~panini/)
- [Original Manual Page](http://web.cs.iastate.edu/~panini/man/)
- [Senior Design Project Page](http://dec1512.sd.ece.iastate.edu/)
