package edu.rice.habanero.benchmarks.apsp;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class ApspTemplate
{
    @Local Master master;

    public void run() {
        master.start();
    }
}
