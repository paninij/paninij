package edu.rice.habanero.benchmarks.philosopher;

public class PhilosopherBenchmark
{
    public static void main(String[] args) {
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
        PhilosopherLiftActorBenchmark.main(args);
    }
}
