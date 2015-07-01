package org.paninij.apt.shapes.interfaces;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.paninij.apt.shapes.interfaces.Tests;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class RunTestsTemplate
{
    @Child Tests test;

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
        catch (InterruptedException | ExecutionException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
