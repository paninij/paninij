package edu.rice.habanero.benchmarks.astar;

public class GuidedSearchBenchmark
{

    public static void main(String[] args) {
        GuidedSearchScalaActorBenchmark.main(args);
        GuidedSearchScalazActorBenchmark.main(args);
        GuidedSearchAkkaActorBenchmark.main(args);
        GuidedSearchFuncJavaActorBenchmark.main(args);
        GuidedSearchGparsActorBenchmark.main(args);
        GuidedSearchHabaneroActorBenchmark.main(args);
        GuidedSearchHabaneroSelectorBenchmark.main(args);
        GuidedSearchJetlangActorBenchmark.main(args);
        GuidedSearchJumiActorBenchmark.main(args);
        GuidedSearchAtPaniniJBenchmark.main(args);
        GuidedSearchLiftActorBenchmark.main(args);
    }

}
