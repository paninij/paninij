package edu.rice.habanero.benchmarks.philosopher;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class ArbiterTemplate {

    @Child Philosopher[] philosophers = new Philosopher[PhilosopherConfig.N];

    boolean[] forks = new boolean[PhilosopherConfig.N];
    int numCompletedPhilosophers = 0;

    int numRetries = 0;


    public void design(Arbiter self) {
        for (Philosopher p : philosophers) p.wire(self);
    }

    public void start() {
        for (int i = 0; i < PhilosopherConfig.N; i++) philosophers[i].start(i);
    }

    public void notifyHungry(int id) {

        int r = (id + 1) % PhilosopherConfig.N;
        boolean leftFork = forks[id];
        boolean rightFork = forks[r];

        if (leftFork && rightFork) {
            philosophers[id].deny(id);
            numRetries++;
        } else {
            forks[id] = true;
            forks[r] = true;
            philosophers[id].eat(id);
        }
    }

    public void notifyDone(int id) {
        forks[id] = false;
        forks[(id + 1) % PhilosopherConfig.N] = false;
    }

    public void notifyComplete() {
        numCompletedPhilosophers++;
        if (numCompletedPhilosophers == PhilosopherConfig.N) {
            for (Philosopher p : philosophers) p.exit();
            System.out.println("  Num retries: " + numRetries);
        }
    }

}
