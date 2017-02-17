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

import static org.paninij.examples.pi.Config.SAMPLE_SIZE;
import static org.paninij.examples.pi.Config.WORKER_COUNT;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

/***
 * Calculation of Pi using the Panini language
 *
 * This computation uses the Monte Carlo Method.
 */
@Root
@Capsule
class PiCore
{
    // an array of worker capsules
    @Local Worker[] workers = new Worker[WORKER_COUNT];

    void run() {
        Number[] results = new Number[WORKER_COUNT];

        double total = 0;
        double partition = SAMPLE_SIZE/WORKER_COUNT;


        for (int i = 0; i < WORKER_COUNT; i++)
            results[i] = workers[i].compute(partition);

        for (Number result : results)
            total += result.value();


        double pi = 4.0 * total / SAMPLE_SIZE;

        System.out.println("Estimate for pi using " + SAMPLE_SIZE + " samples: " + pi);
    }
    
    public static void main(String[] args) {
        CapsuleSystem.start(Pi.class, args);
    }
}
