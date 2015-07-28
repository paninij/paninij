package edu.rice.habanero.benchmarks.fjcreate;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class ForkJoinAtPaniniJBenchmark
{
    static class ForkJoinAtPaniniJ extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] arg0) throws IOException {
            // TODO Auto-generated method stub
        }

        @Override
        public void printArgInfo() {
            ForkJoinConfig.printArgs();
        }

        @Override
        public void runIteration() {
            for (int i = 0; i < ForkJoinConfig.N; i++) {
                ForkJoin$Thread.main(null);
                try {
                    Panini$System.threads.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new ForkJoinAtPaniniJ());
    }

}
