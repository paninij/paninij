package org.paninij.examples.helloworld;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
class HelloWorldTemplate
{
    @Child Console c;
    @Child Greeter g;

    // No capsule inputs, so no arguments to this.
    void design(HelloWorld self) {
        g.wire(c);
    }

    // This is an active capsule.
    void run() {
        g.greet();
    }
}
