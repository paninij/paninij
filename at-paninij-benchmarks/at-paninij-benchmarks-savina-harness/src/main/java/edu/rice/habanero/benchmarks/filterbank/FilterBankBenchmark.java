package edu.rice.habanero.benchmarks.filterbank;

public class FilterBankBenchmark
{
    public static void main(String[] args) {
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
        FilterBankLiftActorBenchmark.main(args);
    }
}
