package edu.rice.habanero.benchmarks.count;

public class CountingBenchmark
{
    public static void main(String[] args) {
        CountingScalaActorBenchmark.main(args);
        CountingScalazActorBenchmark.main(args);
        CountingAkkaActorBenchmark.main(args);
        CountingFuncJavaActorBenchmark.main(args);
        CountingGparsActorBenchmark.main(args);
        CountingHabaneroActorBenchmark.main(args);
        CountingHabaneroSelectorBenchmark.main(args);
        CountingJetlangActorBenchmark.main(args);
        CountingJumiActorBenchmark.main(args);
        CountingAtPaniniJBenchmark.main(args);
        CountingAtPaniniJTaskBenchmark.main(args);
        CountingLiftActorBenchmark.main(args);
    }
}
