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

import java.util.Random;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;

@Capsule
public class GameTemplate
{
    short[] asteroidPositions;
    int asteroidPos;
    int lastFired;
    Random prng;

    protected void init() {
        this.asteroidPositions = new short[Constants.WIDTH];
        for (int i = 0; i < this.asteroidPositions.length; i++) {
            this.asteroidPositions[i] = -1;
        }
        this.asteroidPos = -1;
        this.lastFired = -1;
        this.prng = new Random();
    }

    @Block
    public Integer step(int shipPos, boolean isFiring) {
        int result = 0;

        if (asteroidPos == lastFired) {
            result = 1;
        } else if (asteroidPos == shipPos) {
            result = -1;
        }

        this.lastFired = isFiring ? shipPos : -1;
        this.asteroidPos = this.nextAsteroid();
        return result;
    }

    @Block
    private int nextAsteroid() {
        for (int i = Constants.WIDTH - 1; i > 0; i--) {
            asteroidPositions[i] = asteroidPositions[i-1];
        }
        asteroidPositions[0] = (short) prng.nextInt(Constants.WIDTH);
        return asteroidPositions[Constants.WIDTH - 1];
    }

    @Block
    public int getLastFired() {
        return this.lastFired;
    }

    @Block
    public short getAsteroidPosition(int index) {
        return this.asteroidPositions[index];
    }
}
