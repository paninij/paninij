package edu.rice.habanero.benchmarks.radixsort;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class RadixSortBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("RadixSort");
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
        RadixSortAtPaniniJSerialBenchmark.main(args);
        RadixSortLiftActorBenchmark.main(args);
    }
}
