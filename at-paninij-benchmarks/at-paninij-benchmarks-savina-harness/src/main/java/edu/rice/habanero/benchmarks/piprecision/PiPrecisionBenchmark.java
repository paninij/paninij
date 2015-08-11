package edu.rice.habanero.benchmarks.piprecision;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class PiPrecisionBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("PiPrecision");
        PiPrecisionScalaActorBenchmark.main(args);
        PiPrecisionScalazActorBenchmark.main(args);
        PiPrecisionAkkaActorBenchmark.main(args);
        PiPrecisionFuncJavaActorBenchmark.main(args);
        PiPrecisionGparsActorBenchmark.main(args);
        PiPrecisionHabaneroActorBenchmark.main(args);
        PiPrecisionHabaneroSelectorBenchmark.main(args);
        PiPrecisionJetlangActorBenchmark.main(args);
        PiPrecisionJumiActorBenchmark.main(args);
        PiPrecisionAtPaniniJBenchmark.main(args);
        PiPrecisionAtPaniniJTaskBenchmark.main(args);
        PiPrecisionAtPaniniJSerialBenchmark.main(args);
        PiPrecisionLiftActorBenchmark.main(args);
    }
}
