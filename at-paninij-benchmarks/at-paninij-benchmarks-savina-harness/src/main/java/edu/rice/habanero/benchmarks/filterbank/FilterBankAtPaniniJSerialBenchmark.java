package edu.rice.habanero.benchmarks.filterbank;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class FilterBankAtPaniniJSerialBenchmark
{
    static class FilterBankAtPaniniJSerial extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] arg0) throws IOException {
            FilterBankConfig.parseArgs(arg0);
        }

        @Override
        public void printArgInfo() {
            FilterBankConfig.printArgs();
        }

        @Override
        public void runIteration() {
            FilterBank$Serial.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new FilterBankAtPaniniJSerial());
    }
}
