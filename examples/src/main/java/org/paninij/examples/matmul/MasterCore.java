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
import org.paninij.lang.Local;
import org.paninij.lang.Imports;

@Capsule public class MasterCore {
    int numWorkers = Config.NUM_WORKERS;

    @Imports MatrixData data;
    @Local Worker[] workers = new Worker[numWorkers];

    int numWorkSent = 0;
    int numWorkCompleted = 0;

    public void design(Master self) {
        for (Worker w : workers) w.imports(self, data);
    }

    public void start() {
        int len = data.dimension();
        int numBlocks = len * len;
        Work work = new Work(0, 0, 0, 0, 0, 0, 0, numBlocks, len);
        generateWork(work);
    }

    public void workFinished() {
        numWorkCompleted++;
        System.out.println(numWorkCompleted + " / " + numWorkSent);
        if (numWorkCompleted == numWorkSent) {
            for (Worker w : workers) w.exit();
            data.print();
        }
    }

    public void generateWork(Work work) {
        numWorkSent++;
        int indx = (work.srC + work.scC) % numWorkers;
        workers[indx].doWork(work);
    }

}
