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
import org.paninij.lang.Wired;

@Capsule
public class UserInterfaceTemplate {
    @Wired Game game;

    private void paintHorizBorder() {
        for (int i = 0; i <= Constants.WIDTH; i++) System.out.print(Constants.SYMBOL_BORDER);
        System.out.print('\n');
    }

    public void repaint(int shipPos, boolean isFiring, int points) {
        System.out.print('\n');
        this.paintHorizBorder();
        for (int i = 0; i < Constants.WIDTH; i++) {
            for (int j = 0; j < Constants.HEIGHT - 1; j++) {
                if (j == this.game.getAsteroidPosition(i)) {
                    System.out.print(Constants.SYMBOL_ASTEROID);
                } else {
                    System.out.print(Constants.SYMBOL_SPACE);
                }
            }
            System.out.print('\n');
        }

        for (int i = 0; i < Constants.WIDTH; i++) {
            if (i == this.game.getAsteroidPosition(Constants.HEIGHT - 1)) {
                if (i == this.game.getLastFired()) {
                    System.out.print(Constants.SYMBOL_ASTEROID_EXPLODE);
                } else if (i == shipPos) {
                    System.out.print(Constants.SYMBOL_SHIP_EXPLODE);
                } else {
                    System.out.print(Constants.SYMBOL_ASTEROID);
                }
            } else if (i == shipPos) {
                if (isFiring) {
                    System.out.print(Constants.SYMBOL_SHIP_FIRE);
                } else {
                    System.out.print(Constants.SYMBOL_SHIP);
                }
            } else {
                System.out.print(Constants.SYMBOL_SPACE);
            }
        }

        System.out.print('\n');
        this.paintHorizBorder();
        System.out.println("~" + points + "~");

    }

    public void onGameEnd() {
        System.out.println("Game over :(");
    }
}
