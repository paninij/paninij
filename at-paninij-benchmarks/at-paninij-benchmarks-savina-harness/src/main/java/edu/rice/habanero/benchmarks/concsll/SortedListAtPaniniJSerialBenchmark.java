package edu.rice.habanero.benchmarks.concsll;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class SortedListAtPaniniJSerialBenchmark
{
    static class SortedListAtPaniniJSerial extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] args) throws IOException {
            SortedListConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            SortedListConfig.printArgs();
        }

        @Override
        public void runIteration() {
            SLL$Serial.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new SortedListAtPaniniJSerial());
    }
}
