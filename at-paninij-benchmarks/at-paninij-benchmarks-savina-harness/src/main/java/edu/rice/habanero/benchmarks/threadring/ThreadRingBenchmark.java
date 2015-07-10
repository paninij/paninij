package edu.rice.habanero.benchmarks.threadring;

public class ThreadRingBenchmark
{
    public static void main(String[] args) {
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
        ThreadRingLiftActorBenchmark.main(args);
    }
}
