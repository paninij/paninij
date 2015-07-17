package edu.rice.habanero.benchmarks.concdict;

public class DictionaryBenchmark
{
    public static void main(String[] args) {
        DictionaryScalaActorBenchmark.main(args);
        DictionaryScalazActorBenchmark.main(args);
        DictionaryAkkaActorBenchmark.main(args);
        DictionaryFuncJavaActorBenchmark.main(args);
        DictionaryGparsActorBenchmark.main(args);
        DictionaryHabaneroActorBenchmark.main(args);
        DictionaryHabaneroSelectorBenchmark.main(args);
        DictionaryJetlangActorBenchmark.main(args);
        DictionaryJumiActorBenchmark.main(args);
        DictionaryAtPaniniJBenchmark.main(args);
        DictionaryLiftActorBenchmark.main(args);
    }
}
