package edu.rice.habanero.benchmarks.fjthrput;

public class ThroughputBenchmark
{
    public static void main(String[] args) {
        ThroughputScalaActorBenchmark.main(args);
        ThroughputScalazActorBenchmark.main(args);
        ThroughputAkkaActorBenchmark.main(args);
        ThroughputFuncJavaActorBenchmark.main(args);
        ThroughputGparsActorBenchmark.main(args);
        ThroughputHabaneroActorBenchmark.main(args);
        ThroughputHabaneroSelectorBenchmark.main(args);
        ThroughputJetlangActorBenchmark.main(args);
        ThroughputJumiActorBenchmark.main(args);
        ThroughputAtPaniniJBenchmark.main(args);
        ThroughputLiftActorBenchmark.main(args);
    }
}
