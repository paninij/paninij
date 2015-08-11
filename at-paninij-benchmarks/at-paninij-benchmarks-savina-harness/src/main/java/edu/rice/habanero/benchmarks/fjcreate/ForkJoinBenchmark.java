package edu.rice.habanero.benchmarks.fjcreate;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class ForkJoinBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("ForkJoin");
        ForkJoinScalaActorBenchmark.main(args);
        ForkJoinScalazActorBenchmark.main(args);
        ForkJoinAkkaActorBenchmark.main(args);
        ForkJoinFuncJavaActorBenchmark.main(args);
        ForkJoinGparsActorBenchmark.main(args);
        ForkJoinHabaneroActorBenchmark.main(args);
        ForkJoinHabaneroSelectorBenchmark.main(args);
        ForkJoinJetlangActorBenchmark.main(args);
        ForkJoinJumiActorBenchmark.main(args);
        ForkJoinAtPaniniJBenchmark.main(args);
        ForkJoinAtPaniniJTaskBenchmark.main(args);
        ForkJoinAtPaniniJSerialBenchmark.main(args);
        ForkJoinLiftActorBenchmark.main(args);
    }
}
