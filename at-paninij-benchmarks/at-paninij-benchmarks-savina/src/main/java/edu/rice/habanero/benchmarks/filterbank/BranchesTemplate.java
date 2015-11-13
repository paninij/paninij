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

@Capsule public class BranchesTemplate {
    @Imports Integrator integrator;
    @Local Bank[] banks = new Bank[FilterBankConfig.NUM_CHANNELS];

    int numChannels = FilterBankConfig.NUM_CHANNELS;
    int numColumns = FilterBankConfig.NUM_COLUMNS;
    double[][] H =  FilterBankConfig.H;
    double[][] F = FilterBankConfig.F;

    public void design(Branches self) {
        for (int i = 0; i < banks.length; i++) {
            banks[i].imports(i, numColumns, FilterBankConfig.H[i], FilterBankConfig.F[i], integrator);
        }
    }

    public void process(double value) {
        for (Bank b : banks) b.process(value);
    }

}
