package edu.rice.habanero.benchmarks.fjcreate;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.hj.runtime.config.HjSystemProperty;

public class ForkJoinAtPaniniJTaskBenchmark
{
    static class ForkJoinAtPaniniJTask extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] arg0) throws IOException {
            Panini$System.POOL_SIZE = Integer.parseInt(HjSystemProperty.numWorkers.getPropertyValue());
        }

        @Override
        public void printArgInfo() {
            ForkJoinConfig.printArgs();
        }

        @Override
        public void runIteration() {
//            for (int i = 0; i < ForkJoinConfig.N; i++) {
            System.out.println("START");
                ForkJoin$Task.main(null);
                try {
                    Panini$System.threads.await();
                    System.out.println("DONE");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new ForkJoinAtPaniniJTask());
    }
}