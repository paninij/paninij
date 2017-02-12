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

import java.util.HashMap;
import java.util.Map;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

import edu.rice.habanero.benchmarks.BenchmarkRunner;

@Capsule public class DictionaryCore {
    @Imports Worker[] workers;
    Map<Integer, Integer> dataMap = new HashMap<Integer, Integer>(DictionaryConfig.DATA_MAP);

    public void write(int key, int value, int id) {
        dataMap.put(key, value);
        workers[id].doWork();
    }

    public void read(int key, int id) {
        Integer val = dataMap.get(key);
        val = val == null ? -1 : val;
        workers[id].doWork();
    }

    public void printResult() {
        System.out.printf(BenchmarkRunner.argOutputFormat, "Dictionary Size", dataMap.size());
        for (Worker w : workers) w.exit();
    }

}
