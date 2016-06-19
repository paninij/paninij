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
package org.paninij.examples.matmul;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class WorkerTemplate {
    @Imports Master master;
    @Imports MatrixData data;

    public void doWork(Work work) {
        int srA = work.srA;
        int scA = work.scA;
        int srB = work.srB;
        int scB = work.scB;
        int srC = work.srC;
        int scC = work.scC;
        int numBlocks = work.numBlocks;
        int dim = work.dim;
        int newPriority = work.priority + 1;

        if (numBlocks > Config.THRESHOLD) {
            int zerDim = 0;
            int newDim = dim / 2;
            int newNumBlocks = numBlocks / 4;

            master.generateWork(new Work(newPriority, srA + zerDim, scA + zerDim, srB + zerDim, scB + zerDim, srC + zerDim, scC + zerDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + zerDim, scA + newDim, srB + newDim, scB + zerDim, srC + zerDim, scC + zerDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + zerDim, scA + zerDim, srB + zerDim, scB + newDim, srC + zerDim, scC + newDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + zerDim, scA + newDim, srB + newDim, scB + newDim, srC + zerDim, scC + newDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + newDim, scA + zerDim, srB + zerDim, scB + zerDim, srC + newDim, scC + zerDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + newDim, scA + newDim, srB + newDim, scB + zerDim, srC + newDim, scC + zerDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + newDim, scA + zerDim, srB + zerDim, scB + newDim, srC + newDim, scC + newDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + newDim, scA + newDim, srB + newDim, scB + newDim, srC + newDim, scC + newDim, newNumBlocks, newDim));
        } else {
            int endR = srC + dim;
            int endC = scC + dim;

            for (int i = srC; i < endR; i++) {
                for (int j = scC; j < endC; j++) {
                    for (int k = 0; k < dim; k++) {
                        double a = data.getA(i, scA + k);
                        double b = data.getB(srB + k, j);
                        data.addC(i, j, a * b);
                    }
                }
            }
        }
        master.workFinished();
    }

}
