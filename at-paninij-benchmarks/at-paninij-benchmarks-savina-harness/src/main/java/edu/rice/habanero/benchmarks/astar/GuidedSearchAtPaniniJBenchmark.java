package edu.rice.habanero.benchmarks.astar;

import java.io.IOException;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;

public class GuidedSearchAtPaniniJBenchmark
{
    static class GuidedSearchAtPaniniJ extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            boolean valid = GuidedSearchConfig.validate();
            System.out.printf(BenchmarkRunner.argOutputFormat, "Result valid", valid);
            GuidedSearchConfig.initializeData();
        }

        @Override
        public void initialize(String[] args) throws IOException {
            GuidedSearchConfig.parseArgs(args);
        }

        @Override
        public void printArgInfo() {
            GuidedSearchConfig.printArgs();
            int nodesProcessed = GuidedSearchConfig.nodesProcessed();
            track("Nodes Processed", nodesProcessed);
        }

        @Override
        public void runIteration() {
            GuidedSearch$Thread.main(null);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new GuidedSearchAtPaniniJ());
    }
}
