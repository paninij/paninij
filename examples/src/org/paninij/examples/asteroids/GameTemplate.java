package org.paninij.examples.asteroids;

import java.util.Random;

import org.paninij.lang.Capsule;

@Capsule
public class GameTemplate {
    private short[] asteroidPositions;
    private int asteroidPos;
    private int lastFired;
    private Random prng;

    protected void init() {
        this.asteroidPositions = new short[Constants.WIDTH];
        for (int i = 0; i < this.asteroidPositions.length; i++) {
            this.asteroidPositions[i] = -1;
        }
        this.asteroidPos = -1;
        this.lastFired = -1;
        this.prng = new Random();
    }

    public int step(int shipPos, boolean isFiring) {
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

    private int nextAsteroid() {
        for (int i = Constants.WIDTH - 1; i > 0; i--) {
            asteroidPositions[i] = asteroidPositions[i-1];
        }
        asteroidPositions[0] = (short) prng.nextInt(Constants.WIDTH);
        return asteroidPositions[Constants.WIDTH - 1];
    }

    public int getLastFired() {
        return this.lastFired;
    }

    public short getAsteroidPosition(int index) {
        return this.asteroidPositions[index];
    }
}
