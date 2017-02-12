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

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class MasterTemplate {

    long solutionsLimit = NQueensConfig.SOLUTIONS_LIMIT;
    int numWorkers = NQueensConfig.NUM_WORKERS;

    @Local Worker[] workers = new Worker[numWorkers];

    int numWorkersTerminated = 0;
    int numWorkSent = 0;
    int numWorkRecieved = 0;
    int messageCounter = 0;
    long resultCounter = 0;


    public void design(Master self) {
        for (Worker w : workers) w.imports(self);
    }

    public void start() {
        int[] inArray = new int[0];
        sendWork(inArray, 0);
    }

    public void workerDone() {
        numWorkRecieved++;
        if (numWorkRecieved == numWorkSent) {
            goalReached();
        }
    }

    public void sendWork(int[] arr, int depth) {
        numWorkSent++;
        workers[messageCounter].nqueensKernalPar(arr, depth);
        messageCounter = (messageCounter + 1) % numWorkers;
    }

    public void result() {
        resultCounter++;
        if (resultCounter == solutionsLimit) {
            goalReached();
        }
    }

    public void terminate() {
        numWorkersTerminated++;
    }

    private void goalReached() {
        Result.RESULT = resultCounter;
        for (Worker w : workers) {
            w.terminate();
            w.exit();
        }
    }
}
