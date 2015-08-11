package edu.rice.habanero.benchmarks.nqueenk;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class NQueensBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("NQueens");
        NQueensScalaActorBenchmark.main(args);
        NQueensScalazActorBenchmark.main(args);
        NQueensAkkaActorBenchmark.main(args);
        NQueensFuncJavaActorBenchmark.main(args);
        NQueensGparsActorBenchmark.main(args);
        NQueensHabaneroActorBenchmark.main(args);
        NQueensHabaneroSelectorBenchmark.main(args);
        NQueensJetlangActorBenchmark.main(args);
        NQueensJumiActorBenchmark.main(args);
        NQueensAtPaniniJBenchmark.main(args);
        NQueensAtPaniniJTaskBenchmark.main(args);
        NQueensAtPaniniJSerialBenchmark.main(args);
        NQueensLiftActorBenchmark.main(args);
    }
}
