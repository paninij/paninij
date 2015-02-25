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

    /*
     * Move any state which is passed into this capsule; this capsules subsequently owns args.
     * There can be many `init()` functions, but at the end of any `init()` function, all state
     * variables must have been initialized. A zero-arg `init()` function must be defined for every
     * capsule which has one or more state variables.
     */
    public void init() { /* no state inputs */ }

    // Nothing to do, since there are no capsules which need to be passed to this capsule.
    public void design() { /* no capsule inputs */ }

    /**
     * Wire this up with other shared resources. (This does not include code to instantiate
     * capsules which are part of the design.)
     */
    public void wire(Console c, Greeter g) {
        g.design(c);
    }

    public void run()
    {
        g.greet();
    }
}
