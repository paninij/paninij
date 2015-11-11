package edu.rice.habanero.benchmarks.nqueenk;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.habanero.benchmarks.nqueenk.NQueensConfig;


public class NQueensAtPaniniJBenchmark
{
    static class NQueensAtPaniniJ extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            Result.RESULT = 0;
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

            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long expSolution = NQueensConfig.SOLUTIONS[NQueensConfig.SIZE - 1];
            long actSolution = Result.RESULT;
            int solutionsLimit = NQueensConfig.SOLUTIONS_LIMIT;
            boolean valid = actSolution >= solutionsLimit && actSolution <= expSolution;

            System.out.printf(BenchmarkRunner.argOutputFormat, "Solutions found", actSolution);
            System.out.printf(BenchmarkRunner.argOutputFormat, "Result valid", valid);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new NQueensAtPaniniJ());
    }
}
