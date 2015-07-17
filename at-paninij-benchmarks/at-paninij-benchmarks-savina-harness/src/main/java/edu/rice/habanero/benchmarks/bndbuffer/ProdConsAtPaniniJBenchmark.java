package edu.rice.habanero.benchmarks.bndbuffer;

import java.io.IOException;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class ProdConsAtPaniniJBenchmark
{
    static class ProdConsAtPaniniJ extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] args) throws IOException {
            ProdConsBoundedBufferConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            ProdConsBoundedBufferConfig.printArgs();
        }

        @Override
        public void runIteration() {
            ProdCons$Thread.main(null);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new ProdConsAtPaniniJ());
    }
}
