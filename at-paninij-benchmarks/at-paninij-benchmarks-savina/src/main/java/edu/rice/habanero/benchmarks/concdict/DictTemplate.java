package edu.rice.habanero.benchmarks.concdict;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class DictTemplate
{
    @Local Master master;

    public void run() {
        master.start();
    }

}
