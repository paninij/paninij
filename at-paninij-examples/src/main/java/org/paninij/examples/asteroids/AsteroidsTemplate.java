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

package org.paninij.examples.asteroids;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class AsteroidsTemplate {

    @Local Game game;
    @Local Ship ship;
    @Local Input input;
    @Local UserInterface ui;

    public void design(Asteroids self) {
        input.imports(ship);
        ui.imports(game);
    }

    public void run() {
        int points = 0;
        while (ship.isAlive()) {
            int shipPos = ship.getPosition();
            boolean isFiring = ship.isFiring();
            int result = game.step(shipPos, isFiring);

            if (result > 0) {
                points += result;
            } else if (result < 0) {
                ship.die();
            }

            this.ui.repaint(shipPos, isFiring, points);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.ui.onGameEnd();
    }


    public static void main(String[] args) {
        CapsuleSystem.start(Asteroids.class, args);
    }
}
