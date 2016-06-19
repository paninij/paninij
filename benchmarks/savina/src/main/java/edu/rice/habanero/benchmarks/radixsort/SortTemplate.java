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
import org.paninij.lang.Imports;

import edu.rice.habanero.benchmarks.radixsort.RadixSortConfig;


@Capsule public class SortTemplate implements AdderTemplate {
    @Imports Adder next;
    @Imports Long radix;

    int valuesSoFar = 0;

    long[] orderingArray = new long[RadixSortConfig.N];
    int j = 0;

    @Override
    public void add(long value) {
        valuesSoFar++;

        if ((value & radix) == 0) {
            next.add(value);
        } else {
            orderingArray[j] = value;
            j++;
        }

        if (valuesSoFar == RadixSortConfig.N) {
            for (int i = 0; i < j; i++) {
                next.add(orderingArray[i]);
            }
        }

    }
}
