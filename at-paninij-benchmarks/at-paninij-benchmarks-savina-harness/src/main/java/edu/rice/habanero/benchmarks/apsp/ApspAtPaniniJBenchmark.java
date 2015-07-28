package edu.rice.habanero.benchmarks.apsp;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class ApspAtPaniniJBenchmark
{
    static class ApspAtPaniniJ extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            ApspUtils.generateGraph();
        }

        @Override
        public void initialize(String[] args) throws IOException {
            ApspConfig.parseArgs(args);
            ApspUtils.generateGraph();
        }

        @Override
        public void printArgInfo() {
            ApspConfig.printArgs();
        }

        @Override
        public void runIteration() {
            Apsp$Thread.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new ApspAtPaniniJ());
    }
}
