package edu.rice.habanero.benchmarks.philosopher;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.habanero.benchmarks.philosopher.PhilosopherAtPaniniJBenchmark.PhilosopherAtPaniniJ;
import edu.rice.hj.runtime.config.HjSystemProperty;

public class PhilosopherAtPaniniJTaskBenchmark
{
    static class PhilosopherAtPaniniJTask extends Benchmark {

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
            PhilosopherConfig.printArgs();
        }

        @Override
        public void runIteration() {
            Master$Task.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new PhilosopherAtPaniniJTask());
    }
}
