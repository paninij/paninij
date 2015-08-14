package edu.rice.habanero.benchmarks.big;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.habanero.benchmarks.big.BigAtPaniniJTaskBenchmark.BigAtPaniniJTask;
import edu.rice.hj.runtime.config.HjSystemProperty;

public class BigAtPaniniJSerialBenchmark
{
    static class BigAtPaniniJSerial extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] args) throws IOException {
            BigConfig.parseArgs(args);
            Panini$System.POOL_SIZE = Integer.parseInt(HjSystemProperty.numWorkers.getPropertyValue());
        }

        @Override
        public void printArgInfo() {
            BigConfig.printArgs();
        }

        @Override
        public void runIteration() {
            Big$Serial.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new BigAtPaniniJSerial());
    }
}
