package org.paninij.examples.asteroids;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class AsteroidsTemplate {

    @Child Game game;
    @Child Ship ship;
    @Child Input input;
    @Child UserInterface ui;

    public void design(Asteroids self) {
        input.wire(ship);
        ui.wire(game);
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



}
