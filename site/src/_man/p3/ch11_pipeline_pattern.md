---
part: 3
chapter: 11
title: Pipeline Pattern
---

## Overview of pipeline parallelism implementation

Pipeline parallelism is usually employed in situations where computation is done
in a loop, but instead of running each iteration in parallel, the computation is
divided into discrete stages and the work associated to each of these stages is
done in parallel.

In languages with an explicit model of concurrency the programmer would wind up
having to map each stage to one or more threads, all this while ensuring that
the threads operate on different stages from different operations. On top of all
this, the programmer might also want to organize the program in such a way that
the stages of the pipeline are easily interchangeable (when it makes sense),
replaceable and ultimately have the program be maintainable.

Panini’s features allow the programmer to focus on the latter issues that deal
with design and maintainability while the compiler will implicitely provide
concurrency where possible. To illustrate, consider the problem of maintaining
the running average, total, minimum and maximum price of a stock in a day.

## Architecture and design

In capsule-oriented programming better design leads to better implicit concurrency, i.e. better designed programs often run faster, so it is valuable to start off with the architecture and design. As an overview, the Panini programmer specifies a design block as a collection of capsules, signatures and ordinary object-oriented classes. A capsule is an abstraction for decomposing a software into its parts and a design block is a mechanism for composing these parts together. So the first order of business is to come up with this capsule-oriented design. This involves creating capsules and assigning subtasks to these capsules. To start off:

1.  Divide the problems into subproblems. In our case:
    a. computing average, sum, min, max
    b. generate multiple values and feed them through the pipeline

2.  Make key design decisions. In our case, we want to be able to easily create
    a program that can compute either of the above values in any order. To that
    end, Panini allows us to declare a signature which allows us to define a
    common interface for capsules.

3.  Create signatures, capsules and assign responsibilities to capsules. We will
    start by defining a signature Stage. It declares two procedure that will
    have to be implemented by any capsules that wants to implement it, the
    semantics are similar to that of Java interfaces.

    **Listing 11.1:** *Signature of any of all our pipeline stages*
    ``` java
    signature Stage {
        // handles pipeline stage input
        void consume(long n);
        // reports the current state of the pipeline stage
        void report();
    }
    ```

    Now that we have a signature we can create the capsules that represent the
    pipeline stages. Each of the stages that are interchangeable expect a Stage
    parameter so that they can be composed freely:

    **Listing 11.2:** *Definition of concrete capsules*
    ``` java
    capsule Average(Stage next) implements Stage {...}
    capsule Sum(Stage next) implements Stage {...}
    capsule Min(Stage next) implements Stage {...}
    capsule Max(Stage next) implements Stage {...}
    //we create an additional stage that is used to seal off the pipeline
    capsule Sink() implements Stage {...}
    ```

    And the only capsule left to define is the one that feeds numbers into the
    pipeline:

    **Listing 11.3:** *Pipeline capsule*
    ``` java
    capsule Pipeline(){...}
    ```

4.  Integrate capsules to form a design block. We know that we need one Pipeline
    capsule and at least one sink, all other capsules could be composed as often
    and in any way we would want to. For no reason other than simply
    demonstrating everything we will use one capsule of each.

    **Listing 11.4:** *Definition of design block*
    ``` java
    capsule Pipeline() {
      design {
        Average avg; Sum sum; Min min; Max max; Sink sink;
        avg(sum); sum(min); min(max); max(snk); sink(num);
      }
      void run() {...}
    }
    ```

    Every capsule can have a design block, it effectively marks the capsule as a
    high level component that is composed out of other capsules. In our case,
    the best choice would be to give the Pipeline capsule such a block. This
    declarative design block (lines 2-5) declares one of each Stage capsule
    types (line 3). On line 4 we link each pipeline stage in the order: Average �
    Sum � Min � Max � Sink.

## Implementation

### Capsules implementing Stage

The behaviour of these capsules is fairly straightforward. Every time the
consume is called they accumulate state and then call the consume procedure on
the next capsule (line 4) in the pipeline. They behave in a similar manner for
the report procedure as well.

**Listing 11.5:** *Implementations of the pipeline stages*
``` java
capsule Sum (Stage next) implements Stage {
    long sum = 0;
    void consume(long n) {
        next.consume(n);
        sum += n;
    }

    void report(){
        next.report();
        System.out.println("Sum␣of␣numbers␣was␣" + sum + ".");
    }
}

capsule Min (Stage next) implements Stage {
    long min = Long.MAX_VALUE;
    void consume(long n) {
        next.consume(n);
        if(n < min) min = n;
    }

    void report(){
        next.report();
        System.out.println("Min␣of␣numbers␣was␣" + min + ".");
    }
}

capsule Max (Stage next) implements Stage {
    long max = 0;
    void consume(long n) {
        next.consume(n);
        if ( n > max) max = n;
    }

    void report() {
        next.report();
        System.out.println("Max␣of␣numbers␣was␣" + max + ".");
    }
}

capsule Sink(long num) implements Stage {
    long count = 0;
    void consume(long n) {
        count++;
    }

    void report() {
        if (count != num)
            throw new RuntimeException("count␣should␣be:␣" + num + ";␣but␣was:␣" + count);
        System.out.println("Successful␣" + count + "␣runs!!");
    }
}
```

The implementation of the compute procedures should be easily understood by any
Java programmer, it has the same syntax. As for the semantics, a call to a
non-void external capsule procedure immediately returns a "future" value, while
the procedure that is called runs concurrently. That value behaves exactly like
normal values, so you won’t need to modify your programs to make adjustments for
it. When you need the actual value, and if the called procedure has completed
running execution proceeds as usual, otherwise execution is blocked until the
called procedure completes running.

Capsule Pipeline Line 10 declares a procedure run, every capsule can optionally
declare such a method and it is implicitely invoked at the start of the program.

**Listing 11.6:** *Implementation of Pipeline*
``` java
capsule Pipeline () {
    int num = 500;

    design {
        Average avg; Sum sum; Min min; Max max; Sink snk;
        avg(sum); sum(min); min(max); max(snk); snk(num);
    }

    Random prng = new Random ();
    void run() {
        for (int j = 0; j < num; j++) {
            long n = prng.nextInt(1024);
            avg.consume(n);
        }
        avg.report();
    }
}
```

The execution of this program begins by allocating memory for all capsule
instances, and connecting them together as specified in the design declaration
(lines 4-7). Recall that capsule parameters define the other capsule instances
required for a capsule to function. A capsule listed in another capsule’s
parameter list or in its design block can be sent messages from that capsule.
Design declarations allow a programmer to define the connections between
individual capsule instances. These connections are established before execution
of any capsule instance begins.

Next, any capsule with a run procedure begins executing independently as soon as
the initialization and interconnection of all capsules is complete and may
generate calls to the procedures of other capsules. For example, capsule
Pipeline will run the code on lines 10-16. Capsules without a run procedure,
such as Max, perform computation only when their procedures are invoked.


## Implicit concurrency

This code is very similar to how one would write a sequential program to model
the same scenario, so the structure of this Panini program would be familiar to
a sequential programmer. This code is also free of any concurrency-related
concerns, such as setup and teardown threads for running each stage in the
pipeline concurrently and synchronization between adjacent stages to hand off
the input to the next stage, which is typical of a pipeline pattern. This code
would, however, have all of the benefits of the explicitly concurrent
implementation. Therefore, we believe that a sequential programmer would have a
greater chance of success when writting such a program in Panini.

The implicit concurrency in this example is on line 13 in the capsule Pipeline,
where calling an external capsule procedure immediately returns. Additionally,
every call to a consume procedure on any Stage type capsules, at any point
throughout the pipeline are subject to implicit concurrency.

When it is safe to exploit these sources of implicit concurrency, Panini
compiler will automatically introduce parallelism to speedup this program
without intervention from the programmer.
