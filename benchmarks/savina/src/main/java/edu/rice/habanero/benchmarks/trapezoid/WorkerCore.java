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

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;

import edu.rice.habanero.benchmarks.trapezoid.TrapezoidalConfig;

@Capsule
public class WorkerTemplate {

    @Future public double process(double left, double right, double partitions) {

        // the sum of areas for this section of partitions
        double area = 0.0;

        int n = (int) ((right - left) / partitions);

        // calculate the `y` values for each partition
        for (int i = 0; i < n; i++) {

            // the left-hand x bound of the partition
            double lx = i * partitions + left;

            // the right-hand x bound of the partition
            double rx = lx + partitions;

            // the `y` value of fx(x) at the left-hand bound
            double ly = TrapezoidalConfig.fx(lx);

            // the `y` value of fx(x) at the right-hand bound
            double ry = TrapezoidalConfig.fx(rx);

            // add to the total area
            area += (ly + ry) * 0.5 * partitions;
        }

        return area;
    }
}
