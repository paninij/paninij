package org.paninij.examples.asteroids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule
public class InputTemplate {

    @Wired Ship ship;
    private BufferedReader in;

    public void init() {
        this.in = new BufferedReader(new InputStreamReader(System.in));
    }

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
            this.in.close();
        } catch (IOException ioe) {}
    }
}
