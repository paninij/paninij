package edu.rice.habanero.benchmarks.big;

public class BigBenchmark
{
    public static void main(String[] args) {
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
        BigLiftActorBenchmark.main(args);
    }
}
