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
package edu.rice.habanero.benchmarks.trapezoid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

import edu.rice.habanero.benchmarks.trapezoid.TrapezoidalConfig;
import org.paninij.lang.Root;

@Root
@Capsule
class TrapezoidCore
{
    // The array of worker capsules
    @Local Worker[] workers = new Worker[TrapezoidalConfig.W];

    void run() {
        // we'll populate this list with results from each worker
        List<Future<Double>> results = new ArrayList<Future<Double>>();

        double range = (TrapezoidalConfig.R - TrapezoidalConfig.L) / TrapezoidalConfig.W;
        double precision = (TrapezoidalConfig.R - TrapezoidalConfig.L) / TrapezoidalConfig.N;

        // we will now tell delegate some partitions to each capsule
        for (int i = 0; i < TrapezoidalConfig.W; i++) {

            double left = range * i + TrapezoidalConfig.L;
            double right = left + range;

            // tell the worker to start processing, and add the Future
            // the our list results
            results.add(workers[i].process(left, right, precision));
        }

        // the sum of all of the worker's sums
        double area = 0.0;

        // loop through each result and add it to the total sum
        try {
            for (Future<Double> result : results) area += result.get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        System.out.println("Area: " + area);
    }

}
