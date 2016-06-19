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
package edu.rice.habanero.benchmarks.nqueenk;

import java.io.IOException;

import org.paninij.runtime.Panini$System;

import edu.rice.habanero.benchmarks.Benchmark;
import edu.rice.habanero.benchmarks.BenchmarkRunner;
import edu.rice.hj.runtime.config.HjSystemProperty;

public class NQueensAtPaniniJTaskBenchmark
{
    static class NQueensAtPaniniJTask extends Benchmark {

        @Override
        public void cleanupIteration(boolean arg0, double arg1) {
            Result.RESULT = 0;
        }

        @Override
        public void initialize(String[] args) throws IOException {
            NQueensConfig.parseArgs(args);
            Panini$System.POOL_SIZE = Integer.parseInt(HjSystemProperty.numWorkers.getPropertyValue());
        }

        @Override
        public void printArgInfo() {
            NQueensConfig.printArgs();
        }

        @Override
        public void runIteration() {
            NQueens$Task.main(null);

            try {
                Panini$System.threads.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long expSolution = NQueensConfig.SOLUTIONS[NQueensConfig.SIZE - 1];
            long actSolution = Result.RESULT;
            int solutionsLimit = NQueensConfig.SOLUTIONS_LIMIT;
            boolean valid = actSolution >= solutionsLimit && actSolution <= expSolution;

            System.out.printf(BenchmarkRunner.argOutputFormat, "Solutions found", actSolution);
            System.out.printf(BenchmarkRunner.argOutputFormat, "Result valid", valid);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runBenchmark(args, new NQueensAtPaniniJTask());
    }
}
