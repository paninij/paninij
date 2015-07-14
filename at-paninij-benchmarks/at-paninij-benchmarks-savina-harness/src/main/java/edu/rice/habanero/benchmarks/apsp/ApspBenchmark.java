package edu.rice.habanero.benchmarks.apsp;

public class ApspBenchmark
{
    public static void main(String[] args) {
        ApspScalaActorBenchmark.main(args);
        ApspScalazActorBenchmark.main(args);
        ApspAkkaActorBenchmark.main(args);
        ApspFuncJavaActorBenchmark.main(args);
        ApspGparsActorBenchmark.main(args);
        ApspHabaneroActorBenchmark.main(args);
        ApspHabaneroSelectorBenchmark.main(args);
        ApspJetlangActorBenchmark.main(args);
        ApspJumiActorBenchmark.main(args);
        ApspAtPaniniJBenchmark.main(args);
        ApspLiftActorBenchmark.main(args);
    }
}
