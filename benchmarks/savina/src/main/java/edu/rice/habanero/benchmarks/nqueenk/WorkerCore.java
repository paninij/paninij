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
import org.paninij.lang.Imported;

@Capsule class WorkerCore {
    @Imported Master master;

    int threshold = NQueensConfig.THRESHOLD;
    int size = NQueensConfig.SIZE;

    void terminate() {
        master.terminate();
    }

    void nqueensKernalPar(int[] a, int depth) {
        if (size == depth) {
            master.result();
        } else if (depth >= threshold) {
            nqueensKernalSeq(a, depth);
        } else {
            int newDepth = depth + 1;
            for (int i = 0; i < size; i++) {
                int[] b = new int[newDepth];
                System.arraycopy(a, 0, b, 0, depth);
                b[depth] = i;
                if (NQueensConfig.boardValid(newDepth, b)) {
                    master.sendWork(b, newDepth);
                }
            }
        }
        master.workerDone();
    }

    private void nqueensKernalSeq(int[] a, int depth) {
        if (size == depth) {
            master.result();
        } else {
            int[] b = new int[depth + 1];
            for (int i = 0; i < size; i++) {
                System.arraycopy(a, 0, b, 0, depth);
                b[depth] = i;
                if (NQueensConfig.boardValid(depth + 1,  b)) {
                    nqueensKernalSeq(b, depth + 1);
                }
            }
        }
    }

}
