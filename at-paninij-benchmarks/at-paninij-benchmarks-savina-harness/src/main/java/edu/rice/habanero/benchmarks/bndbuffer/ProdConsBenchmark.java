package edu.rice.habanero.benchmarks.bndbuffer;

public class ProdConsBenchmark
{
    public static void main(String[] args) {
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
        ProdConsLiftActorBenchmark.main(args);
    }
}
