package org.paninij.benchmarks.savina.util;

import edu.rice.habanero.benchmarks.fjthrput.ThroughputBenchmark;
import edu.rice.habanero.benchmarks.pingpong.PingPongBenchmark;

public class BenchmarkSuiteRunner {
    public static void main(String[] args) {
        final int ITERATIONS = 4;
        args = new String[]{"-iter", "" + ITERATIONS};

        ThroughputBenchmark.main(args);

        PingPongBenchmark.main(args);



    }
}
