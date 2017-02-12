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
package edu.rice.habanero.benchmarks.apsp;

import java.util.ArrayList;
import java.util.List;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class MasterCore {
    long[][] graphData = ApspUtils.graphData();
    int numNodes = ApspConfig.N;
    int blockSize = ApspConfig.B;
    int numBlocksInSingleDim = numNodes / blockSize;
    int numWorkers = numBlocksInSingleDim * numBlocksInSingleDim;
    int numWorkersFinished = 0;


    // NOTE we do not have 2d arrays of child/wired capsules yet, so cram into one
    @Local Worker[] workers = new Worker[numWorkers];

    public void design(Master self) {
        for (int bi = 0; bi < numBlocksInSingleDim; bi++) {
            for (int bj = 0; bj < numBlocksInSingleDim; bj++) {

                List<Worker> neighbors = new ArrayList<Worker>();

                // add neighbors in same column
                for (int r = 0; r < numBlocksInSingleDim; r++) {
                    if (r != bi) neighbors.add(workers[(r * numBlocksInSingleDim) + bj]);
                }

                // add neighbors in same row
                for (int c = 0; c < numBlocksInSingleDim; c++) {
                    if (c != bj) neighbors.add(workers[(bi * numBlocksInSingleDim) + c]);
                }

                Worker[] n = new Worker[neighbors.size()];
                for (int i = 0; i < neighbors.size(); i++) {
                    n[i] = neighbors.get(i);
                }

                int id = (numBlocksInSingleDim * bi) + bj;

                workers[id].imports(self, n, id);
            }
        }
    }

    public void start() {
        for (Worker w : workers) w.initialize(graphData);
        for (Worker w : workers) w.start();
    }

    public void workerFinished() {
        numWorkersFinished++;
        if (numWorkersFinished == numWorkers) {
            for (Worker w : workers) w.exit();
        }
    }

}
