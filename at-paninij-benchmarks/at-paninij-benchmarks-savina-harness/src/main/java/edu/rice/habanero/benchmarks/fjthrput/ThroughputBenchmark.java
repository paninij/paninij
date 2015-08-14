package edu.rice.habanero.benchmarks.fjthrput;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class ThroughputBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("Throughput");
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
        ThroughputAtPaniniJTaskBenchmark.main(args);
        ThroughputLiftActorBenchmark.main(args);
    }
}
