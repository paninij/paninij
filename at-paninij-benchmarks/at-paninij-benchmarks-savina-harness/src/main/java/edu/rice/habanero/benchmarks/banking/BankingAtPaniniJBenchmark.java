package edu.rice.habanero.benchmarks.banking;

import java.io.IOException;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class BankingAtPaniniJBenchmark
{
    static class BankingAtPaniniJ extends Benchmark {

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
            BankingConfig.printArgs();
        }

        @Override
        public void runIteration() {
            Banking$Thread.main(null);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new BankingAtPaniniJ());
    }
}
