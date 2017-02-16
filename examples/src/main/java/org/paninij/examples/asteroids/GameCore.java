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
 *  Dr. Hridesh Rajan,
 *  Dalton Mills,
 *  David Johnston,
 *  Trey Erenberger
 *  Jackson Maddox
 *******************************************************************************/
package org.paninij.examples.asteroids;

import java.util.Random;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Handler;
import org.paninij.lang.Imported;
import org.paninij.lang.Local;
import org.paninij.lang.EventConnection;
import org.paninij.lang.RegisterType;

@Capsule
public class GameCore {
    @Local Step step;
    @Imported View view;
    @Imported Ship ship;

    short[] asteroidPositions;
    int asteroidPos;
    int lastFired;
    int points;
    Random prng;

    boolean stepFiring;
    int stepShipPos;

    EventConnection<Void> onStepConn;

    void design(Game self) {
        onStepConn = step.step().register(self::onStep, RegisterType.READ);
        ship.updatePosition().register(self::onShipMove, RegisterType.READ);
        ship.firing().register(self::onShipFire, RegisterType.READ);
    }

    void init() {
        this.asteroidPositions = new short[Constants.WIDTH];
        for (int i = 0; i < this.asteroidPositions.length; i++) {
            this.asteroidPositions[i] = -1;
        }
        this.asteroidPos = -1;
        this.lastFired = -2;
        this.prng = new Random();
        stepShipPos = ship.getPosition();
        stepFiring = false;
    }
    
    @Block public int getLastFired() {
        return this.lastFired;
    }

    @Block public short getAsteroidPosition(int index) {
        return this.asteroidPositions[index];
    }

    @Handler public void onShipMove(Integer xPos) {
        if (ship.isAlive()) {
            stepShipPos = xPos;
            paint();
        }
    }

    @Handler public void onShipFire(Void v) {
        if (ship.isAlive()) {
            stepFiring = true;
            paint();
        }
    }

    @Handler public void onStep(Void v) {
        if (ship.isAlive()) {
            int result = step();

            if (result > 0) {
                points += result;
            } else if (result < 0) {
                ship.die();
            }

            paint();
        } else {
            view.paintGameEndMessage();
            onStepConn.off();
        }
    }

    private void paint() {
        view.paint(stepShipPos, stepFiring, points);
    }
    
    private int step() {
        int result = 0;

        if (asteroidPos == lastFired) {
            result = 1;
        } else if (asteroidPos == stepShipPos) {
            result = -1;
        }

        this.lastFired = stepFiring ? stepShipPos : -2;
        this.asteroidPos = this.nextAsteroid();
        stepFiring = false;
        return result;
    }

    @Block private int nextAsteroid() {
        for (int i = Constants.WIDTH - 1; i > 0; i--) {
            asteroidPositions[i] = asteroidPositions[i - 1];
        }
        asteroidPositions[0] = (short) prng.nextInt(Constants.WIDTH);
        return asteroidPositions[Constants.WIDTH - 1];
    }
}
