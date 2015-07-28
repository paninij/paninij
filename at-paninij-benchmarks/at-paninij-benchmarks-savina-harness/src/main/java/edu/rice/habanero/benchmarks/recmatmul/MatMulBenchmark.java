package edu.rice.habanero.benchmarks.recmatmul;

public class MatMulBenchmark
{
    public static void main(String[] args) {
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
        MatMulLiftActorBenchmark.main(args);
    }
}
