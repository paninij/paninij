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
package edu.rice.habanero.benchmarks.concdict;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule class MasterCore {

    @Local Worker[] workers = new Worker[DictionaryConfig.NUM_ENTITIES];
    @Local Dictionary dictionary;

    int numWorkersTerminated = 0;

    void design(Master self) {
        for (int i = 0; i < workers.length; i++) {
            workers[i].imports(self, dictionary, i);
        }
        dictionary.imports(workers);
    }

    void start() {
        for (Worker w : workers) w.doWork();
    }

    void workerFinished() {
        numWorkersTerminated++;
        if (numWorkersTerminated == DictionaryConfig.NUM_ENTITIES) {
            dictionary.printResult();
            for (Worker w : workers) w.exit();
        }
    }
}
