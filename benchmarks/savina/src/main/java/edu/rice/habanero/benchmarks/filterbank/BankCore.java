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
package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Imports;

@Capsule public class BankCore implements ProcessorCore {

    @Imports int sourceId;
    @Imports int numColumns;
    @Imports double[] H;
    @Imports double[] F;
    @Imports Integrator integrator;

    @Local Delay delay1;
    @Local FirFilter fir1;
    @Local SampleFilter sample;
    @Local Delay delay2;
    @Local FirFilter fir2;
    @Local TaggedForward tag;

    public void design(Bank self) {
        delay1.imports(fir1, sourceId + ".1", numColumns - 1);
        fir1.imports(sample, sourceId + ".1", numColumns, H);
        sample.imports(delay2, numColumns);
        delay2.imports(fir2, sourceId + ".2", numColumns -1);
        fir2.imports(tag, sourceId + ".2", numColumns, F);
        tag.imports(integrator, sourceId);
    }

    @Override
    public void process(double value) {
        delay1.process(value);
    }
}
