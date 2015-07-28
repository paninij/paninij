package edu.rice.habanero.benchmarks.pingpong;

public class PingPongBenchmark
{
    public static void main(String[] args) {
        PingPongScalaActorBenchmark.main(args);
        PingPongScalazActorBenchmark.main(args);
        PingPongAkkaActorBenchmark.main(args);
        PingPongFuncJavaActorBenchmark.main(args);
        PingPongGparsActorBenchmark.main(args);
        PingPongHabaneroActorBenchmark.main(args);
        PingPongHabaneroSelectorBenchmark.main(args);
        PingPongJetlangActorBenchmark.main(args);
        PingPongJumiActorBenchmark.main(args);
        PingPongAtPaniniJBenchmark.main(args);
        PingPongAtPaniniJTaskBenchmark.main(args);
        PingPongLiftActorBenchmark.main(args);
    }
}
