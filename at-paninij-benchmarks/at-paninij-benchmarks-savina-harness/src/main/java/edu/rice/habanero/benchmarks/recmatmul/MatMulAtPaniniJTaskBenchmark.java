package edu.rice.habanero.benchmarks.recmatmul;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.habanero.benchmarks.recmatmul.MatMulAtPaniniJBenchmark.MatMulAtPaniniJ;
import edu.rice.hj.runtime.config.HjSystemProperty;

public class MatMulAtPaniniJTaskBenchmark
{
    static class MatMulAtPaniniJTask extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            boolean isValid = MatMulConfig.valid();
            System.out.printf(BenchmarkRunner.argOutputFormat, "Result valid", isValid);
            MatMulConfig.initializeData();
        }

        @Override
        public void initialize(String[] args) throws IOException {
            Panini$System.POOL_SIZE = Integer.parseInt(HjSystemProperty.numWorkers.getPropertyValue());
            MatMulConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            MatMulConfig.printArgs();
        }

        @Override
        public void runIteration() {
            MatMul$Task.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new MatMulAtPaniniJTask());
    }
}
