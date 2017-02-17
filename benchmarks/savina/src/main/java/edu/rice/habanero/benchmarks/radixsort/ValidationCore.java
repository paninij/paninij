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
package edu.rice.habanero.benchmarks.radixsort;

import org.paninij.lang.Capsule;

@Capsule class ValidationCore implements AdderCore {

    int valuesSoFar = 0;
    double sumSoFar = 0.0;
    long prevValue = 0L;

    long errVal = -1;
    int errIndx = -1;

    @Override
    public void add(long value) {
        valuesSoFar++;
        if (value < prevValue && errVal < 0) {
            errVal = value;
            errIndx = valuesSoFar - 1;
        }

        prevValue = value;
        sumSoFar += prevValue;

        if (valuesSoFar == RadixSortConfig.N) {
            if (errVal >= 0) {
                System.out.println("ERROR: Value out of place: " + errVal + " at index " + errIndx);
            } else {
                System.out.println("Elements sum: " + sumSoFar);
            }
        }

    }

}
