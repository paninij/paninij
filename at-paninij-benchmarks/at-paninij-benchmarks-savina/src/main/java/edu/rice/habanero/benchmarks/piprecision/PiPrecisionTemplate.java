package edu.rice.habanero.benchmarks.piprecision;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class PiPrecisionTemplate
{
    @Local Delegator d;

    public void run() {
        d.start();
    }
}
