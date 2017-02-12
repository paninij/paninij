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

import org.paninij.lang.Block;
import org.paninij.lang.Broadcast;
import org.paninij.lang.Capsule;
import org.paninij.lang.Handler;
import org.paninij.lang.Imports;
import org.paninij.lang.Event;
import org.paninij.lang.RegisterType;

@Capsule
public class ShipTemplate {
    @Imports TextAreaUI ui;
    @Broadcast Event<Integer> updatePosition;
    @Broadcast Event<Void> firing;
    
    short state;
    int x;

    protected void init() {
        this.state = 0;
        this.x = Constants.WIDTH/2;
    }
    
    public void design(Ship self) {
        ui.keyPressed().register(self::move, RegisterType.READ);
    }
    
    @Handler public void move(String keyCode) {
        if (!isAlive()) 
            return;
        
        if (keyCode.equals("J")) {
            moveLeft();
        } else if (keyCode.equals("L")) {
            moveRight();
        } else if (keyCode.equals("I")) {
            fire();
        }
    }
    
    public void die() {
        this.state = 2;
    }

    public void fire() {
        this.state = 1;
        firing.announce(null);
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
    
    public void moveLeft() { 
        if (this.x > 0) {
            this.x--;
            updatePosition.announce(x);
        }
    }
    
    public void moveRight() { 
        if (this.x < Constants.WIDTH - 1) {
            this.x++;
            updatePosition.announce(x);
        }
    }
}
