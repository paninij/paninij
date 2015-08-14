package edu.rice.habanero.benchmarks.astar;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class GuidedSearchBenchmark
{

    public static void main(String[] args) {
        BenchmarkSuite.mark("GuidedSearch");
        GuidedSearchScalaActorBenchmark.main(args);
        GuidedSearchScalazActorBenchmark.main(args);
        GuidedSearchAkkaActorBenchmark.main(args);
        GuidedSearchFuncJavaActorBenchmark.main(args);
//        GuidedSearchGparsActorBenchmark.main(args); -- errors out
        GuidedSearchHabaneroActorBenchmark.main(args);
        GuidedSearchHabaneroSelectorBenchmark.main(args);
        GuidedSearchJetlangActorBenchmark.main(args);
        GuidedSearchJumiActorBenchmark.main(args);
        GuidedSearchAtPaniniJBenchmark.main(args);
        GuidedSearchAtPaniniJTaskBenchmark.main(args);
        GuidedSearchAtPaniniJSerialBenchmark.main(args);
        GuidedSearchLiftActorBenchmark.main(args);
    }

}
