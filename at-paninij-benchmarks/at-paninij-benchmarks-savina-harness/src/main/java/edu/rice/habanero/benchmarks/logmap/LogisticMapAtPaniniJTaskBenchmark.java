package edu.rice.habanero.benchmarks.logmap;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.hj.runtime.config.HjSystemProperty;

public class LogisticMapAtPaniniJTaskBenchmark
{
    static class LogisticMapAtPaniniJTask extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
        }

        @Override
        public void initialize(String[] args) throws IOException {
            LogisticMapConfig.parseArgs(args);
            Panini$System.POOL_SIZE = Integer.parseInt(HjSystemProperty.numWorkers.getPropertyValue());
        }

        @Override
        public void printArgInfo() {
            LogisticMapConfig.printArgs();
        }

        @Override
        public void runIteration() {
            LogisticMap$Task.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new LogisticMapAtPaniniJTask());
    }
}
