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
package edu.rice.habanero.benchmarks.logmap;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
class LogisticMapCore
{
    int numTerms = LogisticMapConfig.numTerms;
    int numWorkers = LogisticMapConfig.numSeries;

    @Local SeriesWorker[] workers = new SeriesWorker[numWorkers];

    int numWorkRequested = 0;
    int numWorkRecieved = 0;
    double termsSum = 0;

    void design(LogisticMap self) {
        for (int i = 0; i < numWorkers; i++) {
            double startTerm = i * LogisticMapConfig.increment;
            workers[i].imports(i, startTerm);
        }
    }

    void run() {
        for (int i = 0; i < numTerms; i++) {
            for (SeriesWorker w : workers) w.getTerm();
        }

        for (SeriesWorker w : workers) {
            termsSum += w.getResult();
        }

        System.out.println("Terms sum: " + termsSum);
    }
}
