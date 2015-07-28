package edu.rice.habanero.benchmarks.radixsort;

public class RadixSortBenchmark
{
    public static void main(String[] args) {
        RadixSortScalaActorBenchmark.main(args);
        RadixSortScalazActorBenchmark.main(args);
        RadixSortAkkaActorBenchmark.main(args);
        RadixSortFuncJavaActorBenchmark.main(args);
        RadixSortGparsActorBenchmark.main(args);
        RadixSortHabaneroActorBenchmark.main(args);
        RadixSortHabaneroSelectorBenchmark.main(args);
        RadixSortJetlangActorBenchmark.main(args);
        RadixSortJumiActorBenchmark.main(args);
        RadixSortAtPaniniJBenchmark.main(args);
        RadixSortAtPaniniJTaskBenchmark.main(args);
        RadixSortLiftActorBenchmark.main(args);
    }
}
