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
package edu.rice.habanero.benchmarks.concsll;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

import edu.rice.habanero.benchmarks.BenchmarkRunner;

@Capsule public class SortedListCore {
    @Imports Worker[] workers;
    SortedLinkedList<Integer> dataList = new SortedLinkedList<Integer>();

    public void write(int value, int id) {
        dataList.add(value);
        workers[id].doWork();
    }

    public void size(int id) {
        int size = dataList.size();
        workers[id].doWork();
    }

    public void contains(int value, int id) {
        boolean contains = dataList.contains(value);
        workers[id].doWork();
    }

    public void printResult() {
        System.out.printf(BenchmarkRunner.argOutputFormat, "List Size", dataList.size());
        for (Worker w : workers) w.exit();
    }

}
