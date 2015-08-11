package edu.rice.habanero.benchmarks.fjcreate;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class ForkJoinAtPaniniJSerialBenchmark
{
    static class ForkJoinAtPaniniJSerial extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] args) throws IOException {
            ForkJoinConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            ForkJoinConfig.printArgs();
        }

        @Override
        public void runIteration() {
            for (int i = 0; i < ForkJoinConfig.N; i++) {
                ForkJoin$Serial.main(null);
                try {
                    Panini$System.threads.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new ForkJoinAtPaniniJSerial());
    }
}
