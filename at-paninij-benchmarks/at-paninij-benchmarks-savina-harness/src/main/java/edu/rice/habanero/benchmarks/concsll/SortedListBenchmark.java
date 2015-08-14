package edu.rice.habanero.benchmarks.concsll;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class SortedListBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("SortedList");
        SortedListScalaActorBenchmark.main(args);
        SortedListAtPaniniJTaskBenchmark.main(args);
        SortedListAtPaniniJSerialBenchmark.main(args);
        SortedListAtPaniniJBenchmark.main(args);
        SortedListScalazActorBenchmark.main(args);
        SortedListAkkaActorBenchmark.main(args);
        SortedListFuncJavaActorBenchmark.main(args);
        SortedListGparsActorBenchmark.main(args);
        SortedListHabaneroActorBenchmark.main(args);
        SortedListHabaneroSelectorBenchmark.main(args);
        SortedListJetlangActorBenchmark.main(args);
        SortedListJumiActorBenchmark.main(args);
        SortedListLiftActorBenchmark.main(args);
    }
}
