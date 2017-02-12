package org.paninij.proc.shapes.interfaces;

import java.util.concurrent.Future;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule
public class RunTestsCore
{
    @Local Tests test;

    public void run() {
        MyInterface t = test.getThing();
        MyInterface b = test.blockGetThing();
        Future<MyInterface> f = test.futureGetThing();

        MyInterface thing;
        try
        {
            thing = f.get();
            System.out.println(thing.getTheNumber());
            System.out.println(thing.getTheObject());
            System.out.println(thing.getTheString());
        }
        catch (Throwable ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
