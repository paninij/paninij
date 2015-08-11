package edu.rice.habanero.benchmarks.astar;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.habanero.benchmarks.astar.GuidedSearchAtPaniniJTaskBenchmark.GuidedSearchAtPaniniJTask;
import edu.rice.hj.runtime.config.HjSystemProperty;

public class GuidedSearchAtPaniniJSerialBenchmark
{
    static class GuidedSearchAtPaniniJSerial extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            boolean valid = GuidedSearchConfig.validate();
            System.out.printf(BenchmarkRunner.argOutputFormat, "Result valid", valid);
            GuidedSearchConfig.initializeData();
        }

        @Override
        public void initialize(String[] args) throws IOException {
            GuidedSearchConfig.parseArgs(args);
            Panini$System.POOL_SIZE = Integer.parseInt(HjSystemProperty.numWorkers.getPropertyValue());
        }

        @Override
        public void printArgInfo() {
            GuidedSearchConfig.printArgs();
            int nodesProcessed = GuidedSearchConfig.nodesProcessed();
            track("Nodes Processed", nodesProcessed);
        }

        @Override
        public void runIteration() {
            GuidedSearch$Serial.main(null);
            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new GuidedSearchAtPaniniJSerial());
    }
}
