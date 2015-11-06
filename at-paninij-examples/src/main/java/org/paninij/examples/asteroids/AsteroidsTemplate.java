/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, Hridesh Rajan
 */
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
