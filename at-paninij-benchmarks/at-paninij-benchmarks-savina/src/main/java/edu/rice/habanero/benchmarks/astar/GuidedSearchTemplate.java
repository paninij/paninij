package edu.rice.habanero.benchmarks.astar;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class GuidedSearchTemplate
{
    @Local Master master;

    public void run() {
        master.start();
    }
}
