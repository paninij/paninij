package org.paninij;

import org.openjdk.jmh.annotations.Benchmark;
import edu.rice.habanero.benchmarks.trapezoid.Trapezoid$Thread;

public class TrapezoidBenchmark
{
    @Benchmark
    public void runTrapezoid() {
        Trapezoid$Thread.main(null);
    }
}
