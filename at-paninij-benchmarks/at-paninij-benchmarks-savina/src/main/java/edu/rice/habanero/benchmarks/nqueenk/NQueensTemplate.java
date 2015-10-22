package edu.rice.habanero.benchmarks.nqueenk;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class NQueensTemplate
{
    @Local Master master;

    public void run() {
        master.start();
    }
}
