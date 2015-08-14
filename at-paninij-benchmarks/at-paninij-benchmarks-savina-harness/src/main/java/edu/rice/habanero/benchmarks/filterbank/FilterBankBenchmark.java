package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class FilterBankBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("FilterBank");
        FilterBankScalaActorBenchmark.main(args);
        FilterBankScalazActorBenchmark.main(args);
        FilterBankAkkaActorBenchmark.main(args);
        FilterBankFuncJavaActorBenchmark.main(args);
        FilterBankGparsActorBenchmark.main(args);
        FilterBankHabaneroActorBenchmark.main(args);
        FilterBankHabaneroOrderedSelectorBenchmark.main(args);
        FilterBankHabaneroUnorderedSelectorBenchmark.main(args);
        FilterBankJetlangActorBenchmark.main(args);
        FilterBankJumiActorBenchmark.main(args);
        FilterBankAtPaniniJBenchmark.main(args);
        FilterBankAtPaniniJSerialBenchmark.main(args);
        FilterBankAtPaniniJTaskBenchmark.main(args);
        FilterBankLiftActorBenchmark.main(args);
    }
}
