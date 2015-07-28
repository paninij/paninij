package edu.rice.habanero.benchmarks.fjcreate;

public class ForkJoinBenchmark
{
    public static void main(String[] args) {
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
        ForkJoinLiftActorBenchmark.main(args);
    }
}
