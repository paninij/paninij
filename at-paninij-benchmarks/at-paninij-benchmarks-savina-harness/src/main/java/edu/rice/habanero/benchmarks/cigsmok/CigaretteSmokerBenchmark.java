package edu.rice.habanero.benchmarks.cigsmok;

public class CigaretteSmokerBenchmark
{
    public static void main(String[] args) {
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
        CigaretteSmokerLiftActorBenchmark.main(args);
    }
}
