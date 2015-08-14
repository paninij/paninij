package edu.rice.habanero.benchmarks.threadring;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class ThreadRingBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("ThreadRing");
        ThreadRingScalaActorBenchmark.main(args);
        ThreadRingScalazActorBenchmark.main(args);
        ThreadRingAkkaActorBenchmark.main(args);
        ThreadRingFuncJavaActorBenchmark.main(args);
        ThreadRingGparsActorBenchmark.main(args);
        ThreadRingHabaneroActorBenchmark.main(args);
        ThreadRingHabaneroSelectorBenchmark.main(args);
        ThreadRingJetlangActorBenchmark.main(args);
        ThreadRingJumiActorBenchmark.main(args);
        ThreadRingAtPaniniJBenchmark.main(args);
        ThreadRingAtPaniniJTaskBenchmark.main(args);
        ThreadRingAtPaniniJSerialBenchmark.main(args);
        ThreadRingLiftActorBenchmark.main(args);
    }
}
