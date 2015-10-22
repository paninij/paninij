package edu.rice.habanero.benchmarks.concsll;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class SLLTemplate
{
    @Local Master master;

    public void run() {
        master.start();
    }
}
