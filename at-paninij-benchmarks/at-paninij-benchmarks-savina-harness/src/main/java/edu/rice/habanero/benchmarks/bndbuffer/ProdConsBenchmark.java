package edu.rice.habanero.benchmarks.bndbuffer;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class ProdConsBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("ProdCons");
        ProdConsScalaActorBenchmark.main(args);
        ProdConsScalazActorBenchmark.main(args);
        ProdConsAkkaActorBenchmark.main(args);
        ProdConsFuncJavaActorBenchmark.main(args);
        ProdConsGparsActorBenchmark.main(args);
        ProdConsHabaneroActorBenchmark.main(args);
        ProdConsHabaneroSelectorBenchmark.main(args);
        ProdConsJetlangActorBenchmark.main(args);
        ProdConsJumiActorBenchmark.main(args);
        ProdConsAtPaniniJBenchmark.main(args);
        ProdConsAtPaniniJTaskBenchmark.main(args);
        ProdConsAtPaniniJSerialBenchmark.main(args);
        ProdConsLiftActorBenchmark.main(args);
    }
}
