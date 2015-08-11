package edu.rice.habanero.benchmarks.concdict;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class DictionaryBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("Dictionary");
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
        DictionaryAtPaniniJTaskBenchmark.main(args);
//        DictionaryAtPaniniJSerialBenchmark.main(args); -- StackOverflowError
        DictionaryLiftActorBenchmark.main(args);
    }
}
