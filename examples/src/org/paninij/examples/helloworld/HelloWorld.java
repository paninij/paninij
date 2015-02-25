package org.paninij.examples.helloworld;

import org.paninij.lang.Panini;

@Panini
class HelloWorld
{
    // Capsules: Call to the constructors (if necessary)
    Console c;
    Greeter g;

    // State Variables:
    // <none>

    // Move any state which is passed into this capsule; this capsules subsequently owns args.
    // There can be many `init()` functions, but at the end of any `init()` function, all state
    // variables must have been initialized. A zero-arg `init()` function must be defined for every
    // capsule which has one or more state variables. If there is nothing to be initialized or
    // passed in, then an empty init function should be inferred (i.e. automatically generated).
    public void init() {
        ;  // no state variables to initialize
    }

    // No capsule inputs, so no arguments to this.
    public void design() {
        g.design(c);
    }

    // This is an active capsule.
    public void run()
    {
        g.greet();
    }
}
