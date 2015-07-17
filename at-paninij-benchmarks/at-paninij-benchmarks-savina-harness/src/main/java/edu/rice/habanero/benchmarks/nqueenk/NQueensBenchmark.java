package edu.rice.habanero.benchmarks.nqueenk;

public class NQueensBenchmark
{
    public static void main(String[] args) {
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
        NQueensLiftActorBenchmark.main(args);
    }
}
