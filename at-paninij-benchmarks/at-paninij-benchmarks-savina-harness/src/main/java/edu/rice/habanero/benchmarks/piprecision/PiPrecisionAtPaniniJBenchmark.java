package edu.rice.habanero.benchmarks.piprecision;

import java.io.IOException;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class PiPrecisionAtPaniniJBenchmark
{
    static class PiPrecisionAtPaniniJ extends Benchmark {

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
            PiPrecisionConfig.printArgs();
        }

        @Override
        public void runIteration() {
            PiPrecision$Thread.main(null);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new PiPrecisionAtPaniniJ());
    }
}
