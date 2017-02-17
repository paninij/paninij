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
package edu.rice.habanero.benchmarks.cigsmok;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule class ArbiterCore {
    @Local Smoker[] smokers = new Smoker[CigaretteSmokerConfig.S];
    Random random = new Random(CigaretteSmokerConfig.R * CigaretteSmokerConfig.S);
    int roundsSoFar = 0;

    void start() {
        notifyRandomSmoker();
    }

    void design(Arbiter self ) {
        for (Smoker s : smokers) s.imports(self);
    }

    void notifySmoking() {
        roundsSoFar++;
        if (roundsSoFar >= CigaretteSmokerConfig.R) {
            for (Smoker s : smokers) s.exit();
        } else {
            notifyRandomSmoker();
        }
    }

    private void notifyRandomSmoker() {
        int newSmokerIndex = Math.abs(random.nextInt()) % CigaretteSmokerConfig.S;
        int busyWaitPeriod = random.nextInt(1000) + 10;
        smokers[newSmokerIndex].smoke(busyWaitPeriod);
    }
}
