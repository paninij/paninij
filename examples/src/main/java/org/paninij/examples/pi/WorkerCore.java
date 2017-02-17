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

package org.paninij.examples.pi;

import java.util.Random;

import org.paninij.lang.Capsule;

/**
 * Each Worker capsule computes a fraction of the total number of samples.
 */
@Capsule
class WorkerCore
{
    Random prng;

    void init() {
        this.prng = new Random();
    }

    Number compute(double num) {
        Number _circleCount = new Number();
        for (double j = 0; j < num; j++) {
            double x = this.prng.nextDouble();
            double y = this.prng.nextDouble();
            if ((x * x + y * y) < 1) _circleCount.incr();
        }
        return _circleCount;
    }
}
