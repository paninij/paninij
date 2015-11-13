
package org.paninij.examples.asteroids;

import java.io.IOException;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule
public class InputTemplate {

    @Imports Ship ship;

    public void run() {
        try {
            while (ship.isAlive()) {
                int c = System.in.read();
                switch (c) {
                case 106:
                    ship.moveLeft();
                    break;
                case 108:
                    ship.moveRight();
                    break;
                case 105:
                    ship.fire();
                    break;
                }
            }
        } catch (IOException ioe) {}
    }
}
