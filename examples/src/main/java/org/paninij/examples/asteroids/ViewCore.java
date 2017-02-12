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

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule
public class ViewCore {
    @Imports TextAreaUI ui;
    @Imports Game game;

    private void paintHorizBorder(StringBuilder builder) {
        for (int i = 0; i <= Constants.WIDTH; i++) {
            builder.append(Constants.SYMBOL_BORDER);
        }
        builder.append('\n');
    }

    public void paint(int shipPos, boolean isFiring, int points) {
        StringBuilder builder = new StringBuilder();
        builder.append('\n');
        this.paintHorizBorder(builder);
        for (int i = 0; i < Constants.WIDTH; i++) {
            for (int j = 0; j < Constants.HEIGHT - 1; j++) {
                if (j == this.game.getAsteroidPosition(i)) {
                    builder.append(Constants.SYMBOL_ASTEROID);
                } else {
                    builder.append(Constants.SYMBOL_SPACE);
                }
            }
            builder.append('\n');
        }

        for (int i = 0; i < Constants.WIDTH; i++) {
            if (i == this.game.getAsteroidPosition(Constants.HEIGHT - 1)) {
                if (i == this.game.getLastFired()) {
                    builder.append(Constants.SYMBOL_ASTEROID_EXPLODE);
                } else if (i == shipPos) {
                    builder.append(Constants.SYMBOL_SHIP_EXPLODE);
                } else {
                    builder.append(Constants.SYMBOL_ASTEROID);
                }
            } else if (i == shipPos) {
                if (isFiring) {
                    builder.append(Constants.SYMBOL_SHIP_FIRE);
                } else {
                    builder.append(Constants.SYMBOL_SHIP);
                }
            } else {
                builder.append(Constants.SYMBOL_SPACE);
            }
        }

        builder.append('\n');
        this.paintHorizBorder(builder);
        builder.append("~" + points + "~" + "\n");

        ui.setText(builder.toString());
    }

    public void paintGameEndMessage() {
        ui.setText("Game over :(");
    }
}
