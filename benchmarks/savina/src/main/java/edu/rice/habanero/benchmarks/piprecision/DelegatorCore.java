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
package edu.rice.habanero.benchmarks.piprecision;

import java.math.BigDecimal;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class DelegatorTemplate
{
    @Local Worker[] workers = new Worker[PiPrecisionConfig.NUM_WORKERS];

    BigDecimal pi = BigDecimal.ZERO;
    final BigDecimal tolerance = BigDecimal.ONE.movePointLeft(PiPrecisionConfig.PRECISION);

    int numTermsRequested = 0;
    int numTermsReceived = 0;

    public void design(Delegator self) {
        for (int i = 0; i < workers.length; i++) {
            workers[i].imports(self, i);
        }
    }

    public void start() {
        for (int i = 0; i < PiPrecisionConfig.NUM_WORKERS; i++) {
            generateWork(i);
        }
    }

    private void generateWork(int indx) {
        numTermsRequested++;
        workers[indx].work(numTermsRequested);
    }

    public void resultFinished(BigDecimal result, int indx) {
        numTermsReceived++;
        pi = pi.add(result);
        if (result.compareTo(tolerance) > 0) {
            generateWork(indx);
            return;
        }
        if (numTermsReceived == numTermsRequested) {
            for (Worker w : workers) w.exit();
        }
    }

}
