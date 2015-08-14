package edu.rice.habanero.benchmarks.count;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class CountingBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("Counting");
        CountingAtPaniniJTaskBenchmark.main(args);
        CountingAtPaniniJBenchmark.main(args);
        CountingAtPaniniJSerialBenchmark.main(args);
        CountingScalaActorBenchmark.main(args);
        CountingScalazActorBenchmark.main(args);
        CountingAkkaActorBenchmark.main(args);
        CountingFuncJavaActorBenchmark.main(args);
        CountingGparsActorBenchmark.main(args);
        CountingHabaneroActorBenchmark.main(args);
        CountingHabaneroSelectorBenchmark.main(args);
        CountingJetlangActorBenchmark.main(args);
        CountingJumiActorBenchmark.main(args);
        CountingLiftActorBenchmark.main(args);
    }
}
