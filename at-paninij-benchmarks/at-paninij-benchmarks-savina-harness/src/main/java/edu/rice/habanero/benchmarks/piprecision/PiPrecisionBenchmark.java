package edu.rice.habanero.benchmarks.piprecision;

public class PiPrecisionBenchmark
{
    public static void main(String[] args) {
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
        PiPrecisionLiftActorBenchmark.main(args);
    }
}
