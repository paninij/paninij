package edu.rice.habanero.benchmarks.logmap;

public class LogisticMapBenchmark
{
    public static void main(String[] args) {
//        LogisticMapScalaActorBenchmark.main(args); -- hangs

        LogisticMapScalaManualStashActorBenchmark.main(args);
        LogisticMapScalazActorBenchmark.main(args);

//        LogisticMapScalazManualStashActorBenchmark.main(args); -- error out

        LogisticMapAkkaAwaitActorBenchmark.main(args);

//        LogisticMapAkkaBecomeActorBenchmark.main(args); -- hangs

        LogisticMapAkkaBecomeExtActorBenchmark.main(args);
        LogisticMapAkkaManualStashActorBenchmark.main(args);

//        LogisticMapFuncJavaActorBenchmark.main(args); -- error out

        LogisticMapFuncJavaManualStashActorBenchmark.main(args);
        LogisticMapGparsManualStashActorBenchmark.main(args);
        LogisticMapHabaneroManualStashActorBenchmark.main(args);
        LogisticMapHabaneroManualStashSelectorBenchmark.main(args);
        LogisticMapHabaneroPauseResumeActorBenchmark.main(args);
        LogisticMapHabaneroPauseResumeSelectorBenchmark.main(args);
        LogisticMapHabaneroSelectorBenchmark.main(args);
        LogisticMapJetlangActorBenchmark.main(args);

//        LogisticMapJetlangManualStashActorBenchmark.main(args); -- error out

        LogisticMapAtPaniniJBenchmark.main(args);
    }
}
