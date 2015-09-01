package edu.rice.habanero.benchmarks.philosopher;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class PhilosopherTemplate {
    @Imports Arbiter arbiter;

    int roundsSoFar = 0;

    public void start(int id) {
        arbiter.notifyHungry(id);
    }

    public void eat(int id) {
        roundsSoFar++;
        arbiter.notifyDone(id);
        if (roundsSoFar < PhilosopherConfig.M) {
            arbiter.notifyHungry(id);
        } else {
            arbiter.notifyComplete();
        }
    }

    public void deny(int id) {
        arbiter.notifyHungry(id);
    }

}
