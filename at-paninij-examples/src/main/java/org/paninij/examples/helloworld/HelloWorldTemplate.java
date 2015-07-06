package org.paninij.examples.helloworld;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
class HelloWorldTemplate
{
    @Child Console c;
    @Child Greeter g;

    void design(HelloWorld self) {
        g.wire(c);
    }

    void run() {
        int ret2 = g.greetBlock();
        System.out.println("Greet has blocked and returned: " + ret2);
    }
}
