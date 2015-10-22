package edu.rice.habanero.benchmarks.fjthrput;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class ThroughputTemplate
{
    @Local Worker[] workers = new Worker[ThroughputConfig.A];

    public void run() {
        for (int i = 0; i < ThroughputConfig.N; i++)
            for (Worker w : workers) w.process();
    }

}
