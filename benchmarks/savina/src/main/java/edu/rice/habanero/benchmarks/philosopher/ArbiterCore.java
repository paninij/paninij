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
package edu.rice.habanero.benchmarks.philosopher;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class ArbiterCore {

    @Local Philosopher[] philosophers = new Philosopher[PhilosopherConfig.N];

    boolean[] forks = new boolean[PhilosopherConfig.N];
    int numCompletedPhilosophers = 0;

    int numRetries = 0;


    public void design(Arbiter self) {
        for (Philosopher p : philosophers) p.imports(self);
    }

    public void start() {
        for (int i = 0; i < PhilosopherConfig.N; i++) philosophers[i].start(i);
    }

    public void notifyHungry(int id) {

        int r = (id + 1) % PhilosopherConfig.N;
        boolean leftFork = forks[id];
        boolean rightFork = forks[r];

        if (leftFork && rightFork) {
            philosophers[id].deny(id);
            numRetries++;
        } else {
            forks[id] = true;
            forks[r] = true;
            philosophers[id].eat(id);
        }
    }

    public void notifyDone(int id) {
        forks[id] = false;
        forks[(id + 1) % PhilosopherConfig.N] = false;
    }

    public void notifyComplete() {
        numCompletedPhilosophers++;
        if (numCompletedPhilosophers == PhilosopherConfig.N) {
            for (Philosopher p : philosophers) p.exit();
            System.out.println("  Num retries: " + numRetries);
        }
    }

}
