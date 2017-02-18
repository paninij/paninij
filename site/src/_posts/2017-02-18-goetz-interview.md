---
layout: post
title: Writing Concurrent Programs in Java is Hard
description: >
  Java gives programmer's a false sense of security that breaks down when
  concurrency is involved.
date: 2017-02-18
quote: >
  <p>Concurrent programming simply is harder than sequential programming, and a
  big challenge to that is just people recognizing that. And to some degree,
  the Java language plays a dirty trick on developers, that the fundamental
  values of the language design are simplicity and safety.</p>

  <p>Many features of the language were designed to not allow the programmer to
  turn off safety guarantees. That if it checks every pointer for null when
  it dereferences it; it checks the bounds of the array before you index into
  an array; it checks the type of an object before you cast it. And you can't
  turn these checks off the way that you could in previous languages. So the
  idea is that we [Java language designers] are offering you a much higher
  level of safety. If you do something wrong, we're going to throw an
  exception. We're not going to let you proceed with garbage data.</p>

  <p>And similarly, simplicity. The language design takes away many of the
  things that made C++ more complicated, multiple inheritance being the
  obvious example.</p>

  <p>And then there's threads. Which are neither simple nor safe. If you have
  two threads that access a variable without synchronization, you have a data
  race, but it never throws data race exceptions. It just maybe does the
  wrong thing.</p>

  <p>And so you don't have that level of safety checks working for you that you
  do working with other mechanisms. And the aspects of failures of
  a concurrent systems, that they are probabilistic events rather than
  deterministic ones, is something that undermines simplicity.</p>

  <p>And so, I think that part of it is simply recognizing that when you are
  working in a concurrent environment, this is harder. You need to be more
  careful. You need to code more slowly, carefully review your code, have
  your code reviewed by other people, document your design intent more
  clearly...</p>

  <p>You are in a more dangerous environment, you should be aware of the
  pitfalls.  Work a little bit more slowly. Don't be afraid to ask someone to
  peer review your code. All of these things, while they're not magic bullets
  and they are not technical solutions, they are effective ways to improve
  the quality of concurrent programs.</p>
quote_source: >
  Brian Goetz, Interview with <a href="http://www.se-radio.net/2007/01/episode-44-interview-brian-goetz-and-david-holmes/">SE Radio @ 45:11</a>
---

Even the guy [who wrote the book on it](http://jcip.net/) says so.

<blockquote class="blockquote" style="font-size: 14px;">
  {{ page.quote }}
  <p class="blockquote-footer blockquote-reverse">
  {{ page.quote_source }}
  </p>
</blockquote>
