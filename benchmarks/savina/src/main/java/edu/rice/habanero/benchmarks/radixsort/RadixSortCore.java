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
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class RadixSortCore
{
    int sortCount = (int) (Math.log(RadixSortConfig.M) / Math.log(2));

    @Local Validation validator;
    @Local IntSource source;
    @Local Sort[] sorters = new Sort[sortCount];

    public void design(RadixSort self) {
        long radix = RadixSortConfig.M /2;
        Adder next = validator;

        for (int i = 0; i < sortCount; i++) {
            sorters[i].imports(next, radix);
            next = sorters[i];
            radix /= 2;
        }

        source.imports(next);
    }

    public void run() {
        source.start();
    }
}
