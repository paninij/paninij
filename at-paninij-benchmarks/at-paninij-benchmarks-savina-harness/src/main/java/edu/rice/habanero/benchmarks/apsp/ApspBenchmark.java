package edu.rice.habanero.benchmarks.apsp;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class ApspBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("Apsp");
        ApspScalaActorBenchmark.main(args);
        ApspScalazActorBenchmark.main(args);
        ApspAkkaActorBenchmark.main(args);
        ApspFuncJavaActorBenchmark.main(args);
        ApspGparsActorBenchmark.main(args);
        ApspHabaneroActorBenchmark.main(args);
        ApspHabaneroSelectorBenchmark.main(args);
        ApspJetlangActorBenchmark.main(args);
        ApspJumiActorBenchmark.main(args);
        ApspAtPaniniJBenchmark.main(args);
        ApspAtPaniniJTaskBenchmark.main(args);
        ApspAtPaniniJSerialBenchmark.main(args);
        ApspLiftActorBenchmark.main(args);
    }
}
