package edu.rice.habanero.benchmarks.cigsmok;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class CigaretteSmokerBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("CigaretteSmoker");
        CigaretteSmokerScalaActorBenchmark.main(args);
        CigaretteSmokerScalazActorBenchmark.main(args);
        CigaretteSmokerAkkaActorBenchmark.main(args);
        CigaretteSmokerFuncJavaActorBenchmark.main(args);
        CigaretteSmokerGparsActorBenchmark.main(args);
        CigaretteSmokerHabaneroActorBenchmark.main(args);
        CigaretteSmokerHabaneroSelectorBenchmark.main(args);
        CigaretteSmokerJetlangActorBenchmark.main(args);
        CigaretteSmokerJumiActorBenchmark.main(args);
        CigaretteSmokerAtPaniniJBenchmark.main(args);
        CigaretteSmokerAtPaniniJTaskBenchmark.main(args);
        CigaretteSmokerAtPaniniJSerialBenchmark.main(args);
        CigaretteSmokerLiftActorBenchmark.main(args);
    }
}
