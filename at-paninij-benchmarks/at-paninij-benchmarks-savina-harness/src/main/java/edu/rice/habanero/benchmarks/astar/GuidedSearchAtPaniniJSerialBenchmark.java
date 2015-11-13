/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
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
