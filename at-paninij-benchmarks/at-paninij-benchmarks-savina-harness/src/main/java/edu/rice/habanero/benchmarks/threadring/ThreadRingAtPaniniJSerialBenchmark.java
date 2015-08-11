package edu.rice.habanero.benchmarks.threadring;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.habanero.benchmarks.threadring.ThreadRingAtPaniniJBenchmark.ThreadRingAtPaniniJ;

public class ThreadRingAtPaniniJSerialBenchmark
{
    static class ThreadRingAtPaniniJSerial extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] args) throws IOException {
            ThreadRingConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            ThreadRingConfig.printArgs();
        }

        @Override
        public void runIteration() {
            ThreadRing$Serial.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new ThreadRingAtPaniniJSerial());
    }
}
