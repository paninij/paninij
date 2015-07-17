package edu.rice.habanero.benchmarks.filterbank;

import java.io.IOException;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class FilterBankAtPaniniJBenchmark
{
    static class FilterBankAtPaniniJ extends Benchmark {

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
            FilterBankConfig.printArgs();
        }

        @Override
        public void runIteration() {
            FilterBank$Thread.main(null);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new FilterBankAtPaniniJ());
    }
}
