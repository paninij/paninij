package edu.rice.habanero.benchmarks.logmap;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class LogisticMapAtPaniniJBenchmark
{
    static class LogisticMapAtPaniniJ extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
        }

        @Override
        public void initialize(String[] args) throws IOException {
            LogisticMapConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            LogisticMapConfig.printArgs();
        }

        @Override
        public void runIteration() {
            LogisticMap$Thread.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new LogisticMapAtPaniniJ());
    }
}
