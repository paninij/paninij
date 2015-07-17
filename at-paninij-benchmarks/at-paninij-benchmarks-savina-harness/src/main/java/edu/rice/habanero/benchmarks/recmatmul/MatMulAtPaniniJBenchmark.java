package edu.rice.habanero.benchmarks.recmatmul;

import java.io.IOException;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class MatMulAtPaniniJBenchmark
{
    static class MatMulAtPaniniJ extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            boolean isValid = MatMulConfig.valid();
            System.out.printf(BenchmarkRunner.argOutputFormat, "Result valid", isValid);
            MatMulConfig.initializeData();
        }

        @Override
        public void initialize(String[] args) throws IOException {
            MatMulConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            MatMulConfig.printArgs();
        }

        @Override
        public void runIteration() {
            MatMul$Thread.main(null);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new MatMulAtPaniniJ());
    }
}
