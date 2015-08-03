package edu.rice.habanero.benchmarks.count;

import java.io.IOException;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class CountingAtPaniniJBenchmark
{
    static class CountingAtPaniniJ extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] args) throws IOException {
            CountingConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            CountingConfig.printArgs();
        }

        @Override
        public void runIteration() {
            Count$Thread.main(null);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new CountingAtPaniniJ());
    }
}
