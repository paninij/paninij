---
layout: man/part
part: 2
title: Part II. Panini Language and its Features
---

To recall from previous chapters, capsule-oriented programming and the Panini
language is designed to help programmers deal with the challenges of concurrent
program design. The value proposition of the programming paradigm and the
programming language is twofold:

1. to enable greater program modularity and in doing so automatically enable
greater program concurrency, and

2. improve reasoning about programs in the presence of concurrency.

In fact, Panini does not use explicit concurrency features. Instead, the
programmer modularizes a program using capsules, which implicitly specify
boundaries outside of which concurrency can occur. The Panini runtime will
automatically enable concurrency in between the boundaries of capsules when safe
to do so. We will now discuss each part of the programming language in more
detail.

Panini introduces three main features to extend the Java language. A capsule
declaration, in short capsule, that is designed as a mechanism for decomposing a
program into its parts, a signature declaration that serves as an interface for
capsules. A capsule declaration may optionally contain a design declaration that
is a mechanism for composing instances of capsules to form a subsystem or even
an entire program.

- [Chapter 5. Capsule Declarations](/man/p2/ch5_capsule_declarations.html)
- [Chapter 6. Design Declarations](man/p2/ch6_design_declarations.html)
- [Chapter 7. Signature Declarations](man/p2/ch7_signature_declarations.html)
- [Chapter 8. Capsule State Confinement](man/p2/ch8_capsule_state_confinement.html)
