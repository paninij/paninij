package edu.rice.habanero.benchmarks.cigsmok;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.habanero.benchmarks.cigsmok.CigaretteSmokerAtPaniniJBenchmark.CigaretteSmokerAtPaniniJ;
import edu.rice.hj.runtime.config.HjSystemProperty;

public class CigaretteSmokerAtPaniniJTaskBenchmark
{
    static class CigaretteSmokerAtPaniniJTask extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] args) throws IOException {
            CigaretteSmokerConfig.parseArgs(args);
            Panini$System.POOL_SIZE = Integer.parseInt(HjSystemProperty.numWorkers.getPropertyValue());
        }

        @Override
        public void printArgInfo() {
            CigaretteSmokerConfig.printArgs();
        }

        @Override
        public void runIteration() {
            CigaretteSmoker$Task.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new CigaretteSmokerAtPaniniJTask());
    }
}
