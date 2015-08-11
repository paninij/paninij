package edu.rice.habanero.benchmarks.recmatmul;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class MatMulBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("MatMul");
        MatMulScalaActorBenchmark.main(args);
        MatMulScalazActorBenchmark.main(args);
        MatMulAkkaActorBenchmark.main(args);
        MatMulFuncJavaActorBenchmark.main(args);
        MatMulGparsActorBenchmark.main(args);
        MatMulHabaneroActorBenchmark.main(args);
        MatMulHabaneroSelectorBenchmark.main(args);
        MatMulJetlangActorBenchmark.main(args);
        MatMulJumiActorBenchmark.main(args);
        MatMulAtPaniniJBenchmark.main(args);
        MatMulAtPaniniJTaskBenchmark.main(args);
        MatMulAtPaniniJSerialBenchmark.main(args);
        MatMulLiftActorBenchmark.main(args);
    }
}
