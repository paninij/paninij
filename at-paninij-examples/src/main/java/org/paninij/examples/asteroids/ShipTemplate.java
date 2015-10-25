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

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;

@Capsule
public class ShipTemplate {
    short state;
    int x;

    protected void init() {
        this.state = 0;
        this.x = Constants.WIDTH/2;
    }

    public void die() {
        this.state = 2;
    }

    public void fire() {
        this.state = 1;
    }

    @Block
    public boolean isAlive() {
        return state != 2;
    }

    @Block
    public boolean isFiring() {
        if (this.state == 1) {
            this.state = 0;
            return true;
        }
        return false;
    }

    @Block
    public int getPosition() { return this.x; }
    public void moveLeft() { if (this.x > 0) this.x--; }
    public void moveRight() { if (this.x < Constants.WIDTH) this.x++; }
}
