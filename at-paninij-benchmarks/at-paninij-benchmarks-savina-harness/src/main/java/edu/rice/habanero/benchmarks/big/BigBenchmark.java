package edu.rice.habanero.benchmarks.big;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class BigBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("Big");
        BigScalaActorBenchmark.main(args);
        BigScalazActorBenchmark.main(args);
        BigAkkaActorBenchmark.main(args);
        BigFuncJavaActorBenchmark.main(args);
        BigGparsActorBenchmark.main(args);
        BigHabaneroActorBenchmark.main(args);
        BigHabaneroSelectorBenchmark.main(args);
        BigJetlangActorBenchmark.main(args);
        BigJumiActorBenchmark.main(args);
        BigAtPaniniJBenchmark.main(args);
        BigAtPaniniJTaskBenchmark.main(args);
        BigAtPaniniJSerialBenchmark.main(args);
        BigLiftActorBenchmark.main(args);
    }
}
