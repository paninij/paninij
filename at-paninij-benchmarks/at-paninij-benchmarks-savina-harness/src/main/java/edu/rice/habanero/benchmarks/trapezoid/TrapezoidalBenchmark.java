package edu.rice.habanero.benchmarks.trapezoid;

public class TrapezoidalBenchmark
{
    public static void main(String[] args) {
        TrapezoidalScalaActorBenchmark.main(args);
        TrapezoidalScalazActorBenchmark.main(args);
        TrapezoidalAkkaActorBenchmark.main(args);
        TrapezoidalFuncJavaActorBenchmark.main(args);
        TrapezoidalGparsActorBenchmark.main(args);
        TrapezoidalHabaneroActorBenchmark.main(args);
        TrapezoidalHabaneroSelectorBenchmark.main(args);
        TrapezoidalJetlangActorBenchmark.main(args);
        TrapezoidalJumiActorBenchmark.main(args);
        TrapezoidalAtPaniniJBenchmark.main(args);
        TrapezoidalLiftActorBenchmark.main(args);
    }
}
