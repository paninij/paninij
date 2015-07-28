package edu.rice.habanero.benchmarks.astar;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class GuidedSearchTemplate {
    @Child Master master;

    public void run() {
        master.start();
    }
}
