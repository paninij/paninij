---
part: 3
chapter: 9
title: Master-Worker Pattern
---

## Computing the constant Pi

To illustrate Panini’s new features in practice, consider the classic problem of
computing the constant pi using a Monte Carlo approximation. The idea behind the
method is that we use the ratio, R, between the area of an enclosing square and
the area on an enclosed circle, R  = pi/4. We then proceed to randomly
generating points within the above mentioned area of the square and count how
many of them land in the enclosed circle. The ratio of points that land strictly
within the circle to the total number of points is a good approximation of the
ratio R. We then multiply the result by 4 to get an estimate on the value of pi.

## Architecture and design

In capsule-oriented programming better design leads to better implicit
concurrency, i.e. better designed programs often run faster, so it is valuable
to start off with the architecture and design.

1.  Divide the problems into subproblems. In our case, the subproblems are:
    a. randomly generate a point and test if it’s in the boundary of the circle
    b. aggregate the results

2.  The Panini programmer specifies a system as a collection of capsules and
    ordinary object-oriented classes. A capsule is an abstraction for
    decomposing a software into its parts and a design block is a mechanism for
    composing these parts together. So the first order of business is to come up
    with this capsule-oriented design. This involves creating capsules and
    assigning subtasks to these capsules.

3.  Create capsules and assign responsibilities to capsules. In assigning
    responsibility follow the information-hiding principle. We should have a
    capsule that randomly generates points and tests whether or not they are
    within the circle. A master capsule that gathers the results from all
    generative capsules.

    This suggests capsules: Pi, Worker. For convenience we will be creating a
    wrapper class Number that implicitly handles conversions from integers to
    doubles and back.

    **Listing 9.1:** *Declaration of our capsules*
    ``` java
    capsule Pi(String args[]) { }
    capsule Worker() { }
    ```
    {: .code-with-line-numbers}

    As you can see above, capsule Pi will be the one that receives command line
    parameters.

4.  Integrate capsules to form a design block. We now know that this program
    would require one Pi capsule, and multiple workers. At this time, we can
    make a decision about how many workers we want in this program. In this
    particular case we settle on a fixed number of Worker capsules, 10.

    Every capsule can have a design block, it effectively marks the capsule as a
    high level component that is composed out of other capsules. In our case the
    best choice would be to give the Pi capsule such a block. From the
    description of the problem we can see that the Pi capsule needs to know
    about the Worker capsules, but not the other way around.

    Let us look at the public interfaces of each capsule and the design block:

    **Listing 9.2:** *Public interfaces of the capsules*
    ``` java
    capsule Worker (double batchSize) {
         // Computes the number of points, from a total of batchSize,
         // that fall within the area of the circle
        Number compute(double batchSize) { ... }
    }

    capsule Pi (String[] args) {
        design {
            Worker workers[10];
        }
        void run(){ ... }
    }
    ```
    {: .code-with-line-numbers}

    This declarative design block(lines 10-12) states that the program should
    have a set of 10 Worker capsules.


## Implementation

### Capsule Worker

The behavior of capsule Worker is fairly straightforward: generate a given
number of points and count whether or not they fall within the circle.

To allow other capsules to change its state, a capsule can provide capsule
procedures, procedures for short. A capsule procedure is syntactically similar
to methods in object-oriented languages, however, they are different
semantically in two ways: first, a capsule procedures is by default public
(although private helper procedures can be declared using the private keyword),
and second a procedure invocation is guaranteed to be logically synchronous. In
some cases, Panini may be able to run procedures in parallel to improve
parallelism in Panini programs. In this particular case the only state of our
capsule is the random number generator.

**Listing 9.3:** *Public interfaces of the capsules*
``` java
capsule Worker () {
  Random prng = new Random ();

  Number compute(double num) {
    Number _circleCount = new Number(0);
    for (double j = 0; j < num; j++) {
      double x = prng.nextDouble();
      double y = prng.nextDouble();
      if ((x * x + y * y) < 1) _circleCount.incr();
    }
    return _circleCount;
  }
}
```
{: .code-with-line-numbers}

The implementation of the compute procedure should be easily understood by any
Java programmer, it has the same syntax. As for the semantics, a call to a
non-void external capsule procedure immediately returns a "future" value, while
the procedure that is called runs concurrently. That value behaves exactly like
normal values, so you won’t need to modify your programs to make adjustments for
it. When you need the actual value, and if the called procedure has completed
running execution proceeds as usual, otherwise execution is blocked until the
called procedure completes running.

### Capsule Pi

Line 5 declares a procedure run, every capsule can optionally declare such a
method and it is implicitly invoked at the start of the program.

**Listing 9.4:** *Public interfaces of the capsules*
``` java
capsule Worker () { /* ... */ }
capsule Pi (String[] args) {
  design {
    Worker workers[10];
  }
  void run(){
    if(args.length <= 0) {
      System.out.println("Usage: panini Pi [sample size], try several hundred thousand samples.");
      return;
    }

    double totalSamples = Integer.parseInt(args[0]);
    double startTime = System.currentTimeMillis();
    Number[] results = foreach(Worker w: workers)
        w.compute(totalSamples/workers.length);

    double total = 0;
    for (int i=0; i < workers.length; i++)
      total += results[i].value();

    double pi = 4.0 * total / totalSamples;
    System.out.println("Pi : " + pi);
    double endTime = System.currentTimeMillis();
    System.out.println("Time to compute Pi using " + totalSamples +
                       " samples was:" + (endTime - startTime) + "ms.");
  }
}
```
{: .code-with-line-numbers}

## Implicit concurrency

The implicit concurrency in this example is on line 12 in the capsule Pi, where
calling an external capsule procedure immediately returns, the foreach is simply
a sugar for calling the procedure on a capsule and storing the result in a cell
of an array, one capsule at a time.

On lines 14-15, each indexing of the results array might wind up blocking due to
the fact that the result has not been computed up until that point.

When it is safe to exploit these sources of implicit concurrency, Panini
compiler will automatically introduce parallelism to speedup this program
without intervention from the programmer.
