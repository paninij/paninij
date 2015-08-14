package edu.rice.habanero.benchmarks.radixsort;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class RadixSortAtPaniniJSerialBenchmark
{
    static class RadixSortAtPaniniJSerial extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] args) throws IOException {
            RadixSortConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            RadixSortConfig.printArgs();
        }

        @Override
        public void runIteration() {
            RadixSort$Serial.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new RadixSortAtPaniniJSerial());
    }
}
