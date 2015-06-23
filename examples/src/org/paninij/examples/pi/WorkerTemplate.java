package org.paninij.examples.pi;

import java.util.Random;

import org.paninij.lang.Capsule;

@Capsule
public class WorkerTemplate
{
    Random prng;

    public void init() {
        this.prng = new Random();
    }

    public Number compute(double num) {
        Number _circleCount = new Number();
        for (double j = 0; j < num; j++) {
            double x = this.prng.nextDouble();
            double y = this.prng.nextDouble();
            if ((x * x + y * y) < 1) _circleCount.incr();
        }
        return _circleCount;
    }
}
