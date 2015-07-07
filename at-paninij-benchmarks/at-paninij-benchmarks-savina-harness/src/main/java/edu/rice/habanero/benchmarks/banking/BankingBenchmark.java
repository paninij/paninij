package edu.rice.habanero.benchmarks.banking;

public class BankingBenchmark
{
    public static void main(String[] args) {
//        BankingScalaActorBenchmark.main(args);
//        BankingScalaManualStashActorBenchmark.main(args);
//
//        BankingScalazActorBenchmark.main(args);
//        BankingScalazManualStashActorBenchmark.main(args); -- throws error

//        BankingAkkaAwaitActorBenchmark.main(args);
//        BankingAkkaBecomeActorBenchmark.main(args);
//        BankingAkkaBecomeExtActorBenchmark.main(args);
//        BankingAkkaManualStashActorBenchmark.main(args);
//
//        BankingFuncJavaActorBenchmark.main(args);
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

        BankingLiftManualStashActorBenchmark.main(args);
    }
}
