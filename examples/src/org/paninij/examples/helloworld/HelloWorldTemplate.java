package org.paninij.examples.helloworld;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
        Future<Long> ret = g.greet(true);


        try
        {
            System.out.println("Greet has finished: with " + ret.get());
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        int ret2 = g.greetBlock();
        System.out.println("Greet has blocked and returned: " + ret2);
    }
}
