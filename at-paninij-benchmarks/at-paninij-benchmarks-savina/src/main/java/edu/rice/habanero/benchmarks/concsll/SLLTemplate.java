package edu.rice.habanero.benchmarks.concsll;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class SLLTemplate {
    @Child Master master;

    public void run() {
        master.start();
    }
}
