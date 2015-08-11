package edu.rice.habanero.benchmarks.banking;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class BankingBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("Banking");
        BankingScalaActorBenchmark.main(args);
        BankingScalaManualStashActorBenchmark.main(args);

        BankingScalazActorBenchmark.main(args);
//        BankingScalazManualStashActorBenchmark.main(args); -- throws error

        BankingAkkaAwaitActorBenchmark.main(args);
        BankingAkkaBecomeActorBenchmark.main(args);
        BankingAkkaBecomeExtActorBenchmark.main(args);
        BankingAkkaManualStashActorBenchmark.main(args);

        BankingFuncJavaActorBenchmark.main(args);
//        BankingFuncJavaManualStashActorBenchmark.main(args); -- throws error

        BankingGparsManualStashActorBenchmark.main(args);

        BankingHabaneroManualStashActorBenchmark.main(args);
        BankingHabaneroPauseResumeActorBenchmark.main(args);

        BankingHabaneroManualStashSelectorBenchmark.main(args);
        BankingHabaneroPauseResumeSelectorBenchmark.main(args);

        BankingHabaneroSelectorBenchmark.main(args);

        BankingJetlangActorBenchmark.main(args);
//        BankingJetlangManualStashActorBenchmark.main(args); -- throws error

        BankingJumiManualStashActorBenchmark.main(args);

        BankingAtPaniniJBenchmark.main(args);
        BankingAtPaniniJTaskBenchmark.main(args);
        BankingAtPaniniJSerialBenchmark.main(args);

        BankingLiftManualStashActorBenchmark.main(args);
    }
}
