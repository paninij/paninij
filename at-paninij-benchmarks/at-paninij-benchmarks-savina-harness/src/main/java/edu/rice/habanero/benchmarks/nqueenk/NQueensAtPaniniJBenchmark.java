package edu.rice.habanero.benchmarks.nqueenk;

import java.io.IOException;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.habanero.benchmarks.nqueenk.NQueensConfig;


public class NQueensAtPaniniJBenchmark
{
    static class NQueensAtPaniniJ extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
        }

        @Override
        public void initialize(String[] args) throws IOException {
            NQueensConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            NQueensConfig.printArgs();
        }

        @Override
        public void runIteration() {
            NQueens$Thread.main(null);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new NQueensAtPaniniJ());
    }
}
