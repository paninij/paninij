package org.paninij;

import org.paninij.benchmarks.util.ThreadTimer;

public class TimerTester
{

    public static void main(String[] args)
    {
        ThreadTimer.start(1);
        System.out.println("Do work that will be timed in nanoseconds.");
        ThreadTimer.stop(1);
    }

}
