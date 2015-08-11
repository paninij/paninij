package edu.rice.habanero.benchmarks.nqueenk;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.hj.runtime.config.HjSystemProperty;

public class NQueensAtPaniniJTaskBenchmark
{
    static class NQueensAtPaniniJTask extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            MasterTemplate.RESULT = 0;
        }

        @Override
        public void initialize(String[] args) throws IOException {
            NQueensConfig.parseArgs(args);
            Panini$System.POOL_SIZE = Integer.parseInt(HjSystemProperty.numWorkers.getPropertyValue());
        }

        @Override
        public void printArgInfo() {
            NQueensConfig.printArgs();
        }

        @Override
        public void runIteration() {
            NQueens$Task.main(null);

            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long expSolution = NQueensConfig.SOLUTIONS[NQueensConfig.SIZE - 1];
            long actSolution = MasterTemplate.RESULT;
            int solutionsLimit = NQueensConfig.SOLUTIONS_LIMIT;
            boolean valid = actSolution >= solutionsLimit && actSolution <= expSolution;

            System.out.printf(BenchmarkRunner.argOutputFormat, "Solutions found", actSolution);
            System.out.printf(BenchmarkRunner.argOutputFormat, "Result valid", valid);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new NQueensAtPaniniJTask());
    }
}
