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
package org.paninij.examples.eventasteroids;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Window implements KeyListener {
    private JFrame frame;
    private JTextArea textArea;
    private TextAreaUI ui;

    public Window(TextAreaUI ui) {
        this.ui = ui;
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Asteroid");
            textArea = new JTextArea();

            textArea.setPreferredSize(new Dimension(220, 500));
            textArea.addKeyListener(this);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
            frame.add(textArea);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public void setText(String str) {
        SwingUtilities.invokeLater(() -> {
            textArea.setText(str);
        });
    }

    @Override public void keyPressed(KeyEvent e) {
        ui.keyPressed(e);
    }

    @Override public void keyTyped(KeyEvent e) {
        // Do nothing
    }

    @Override public void keyReleased(KeyEvent e) {
        // Do nothing
    }
}
