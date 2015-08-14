package edu.rice.habanero.benchmarks.fjthrput;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.habanero.benchmarks.fjthrput.ThroughputAtPaniniJBenchmark.ThroughputAtPaniniJ;

public class ThroughputAtPaniniJSerialBenchmark
{
    static class ThroughputAtPaniniJSerial extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] args) throws IOException {
            ThroughputConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            ThroughputConfig.printArgs();
        }

        @Override
        public void runIteration() {
            Throughput$Serial.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new ThroughputAtPaniniJSerial());
    }
}
