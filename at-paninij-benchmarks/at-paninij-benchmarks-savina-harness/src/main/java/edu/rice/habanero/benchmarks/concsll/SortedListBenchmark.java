package edu.rice.habanero.benchmarks.concsll;

public class SortedListBenchmark
{
    public static void main(String[] args) {
        SortedListScalaActorBenchmark.main(args);
        SortedListAtPaniniJTaskBenchmark.main(args);
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
