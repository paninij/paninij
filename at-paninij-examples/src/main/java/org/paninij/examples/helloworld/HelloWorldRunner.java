package org.paninij.examples.helloworld;

import org.paninij.runtime.Panini$System;

public class HelloWorldRunner
{

    public static void main(String[] args)
    {
        HelloWorld$Thread.main(args);
        try
        {
            System.out.println("Startin");
            Panini$System.threads.await();
            System.out.println("Done");
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
