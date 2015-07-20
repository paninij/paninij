package edu.rice.habanero.benchmarks.trapezoid;

import java.io.IOException;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class TrapezoidalAtPaniniJSerialBenchmark
{
    static class TrapezoidalAtPaniniJ extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void initialize(String[] arg0) throws IOException {
            // TODO Auto-generated method stub
        }

        @Override
        public void printArgInfo() {
            TrapezoidalConfig.printArgs();
        }

        @Override
        public void runIteration() {
            Trapezoid$Serial.main(null);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new TrapezoidalAtPaniniJ());
    }
}
