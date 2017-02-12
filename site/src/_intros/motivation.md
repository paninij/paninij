---
title: Motivation
short_title: Motivation
---

[Brian Goetz Interview with SE Radio @ 45:11](http://www.se-radio.net/2007/01/episode-44-interview-brian-goetz-and-david-holmes/):

> Concurrent programming simply is harder than sequential programming, and a
> big challenge to that is just people recognizing that. And to some degree,
> the Java language plays a dirty trick on developers, that the fundamental
> values of the language design are simplicity and safety.
>
> Many features of the language were designed to not allow the programmer to
> turn off safety guarantees. That if it checks every pointer for null when
> it dereferences it; it checks the bounds of the array before you index into
> an array; it checks the type of an object before you cast it. And you can't
> turn these checks off the way that you could in previous languages. So the
> idea is that we [Java language designers] are offering you a much higher
> level of safety. If you do something wrong, we're going to throw an
> exception. We're not going to let you proceed with garbage data.
>
> And similarly, simplicity. The language design takes away many of the
> things that made C++ more complicated, multiple inheritance being the
> obvious example.
>
> And then there's threads. Which are neither simple nor safe. If you have
> two threads that access a variable without sychronization, you have a data
> race, but it never throws data race exceptions. It just maybe does the
> wrong thing.
>
> And so you don't have that level of safety checks working for you that you
> do working with other mechanisms. And the aspects of failures of
> a concurrent systems, that they are probabilistic events rather than
> deterministic ones, is something that underminds simplicity.
>
> And so, I think that part of it is simply recognizing that when you are
> working in a concurrent environment, this is harder. You need to be more
> careful. You need to code more slowly, carefully review your code, have
> your code reviewed by other people, document your design intent more
> clearly...
>
> You are in a more dangerous environment, you should be aware of the
> pitfalls.  Work a little bit more slowly. Don't be afraid to ask someone to
> peer review your code. All of these things, while they're not magic bullets
> and they are not technical solutions, they are effective ways to improve
> the quality of concurrent programs.

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
