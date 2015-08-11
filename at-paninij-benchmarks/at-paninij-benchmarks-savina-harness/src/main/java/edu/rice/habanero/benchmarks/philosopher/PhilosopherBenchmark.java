package edu.rice.habanero.benchmarks.philosopher;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class PhilosopherBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("Philosopher");
        PhilosopherScalaActorBenchmark.main(args);
        PhilosopherScalazActorBenchmark.main(args);
        PhilosopherAkkaActorBenchmark.main(args);
        PhilosopherFuncJavaActorBenchmark.main(args);
        PhilosopherGparsActorBenchmark.main(args);
        PhilosopherHabaneroAsyncActorBenchmark.main(args);
        PhilosopherHabaneroSeqActorBenchmark.main(args);
        PhilosopherHabaneroAsyncSelectorBenchmark.main(args);
        PhilosopherHabaneroSeqSelectorBenchmark.main(args);
        PhilosopherJetlangActorBenchmark.main(args);
        PhilosopherJumiActorBenchmark.main(args);
        PhilosopherAtPaniniJBenchmark.main(args);
        PhilosopherAtPaniniJTaskBenchmark.main(args);
        PhilosopherAtPaniniJSerialBenchmark.main(args);
        PhilosopherLiftActorBenchmark.main(args);
    }
}
