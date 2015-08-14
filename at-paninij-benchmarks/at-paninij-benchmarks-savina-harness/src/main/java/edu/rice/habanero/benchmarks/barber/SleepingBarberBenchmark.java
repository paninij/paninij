package edu.rice.habanero.benchmarks.barber;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class SleepingBarberBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("SleepingBarber");
        SleepingBarberScalaActorBenchmark.main(args);
        SleepingBarberScalazActorBenchmark.main(args);
        SleepingBarberAkkaActorBenchmark.main(args);
        SleepingBarberFuncJavaActorBenchmark.main(args);
        SleepingBarberGparsActorBenchmark.main(args);
        SleepingBarberHabaneroActorBenchmark.main(args);
        SleepingBarberHabaneroSelectorBenchmark.main(args);
        SleepingBarberJetlangActorBenchmark.main(args);
        SleepingBarberJumiActorBenchmark.main(args);
        SleepingBarberAtPaniniJBenchmark.main(args);
        SleepingBarberAtPaniniJSerialBenchmark.main(args);
        SleepingBarberLiftActorBenchmark.main(args);
    }
}
