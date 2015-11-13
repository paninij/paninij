
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
