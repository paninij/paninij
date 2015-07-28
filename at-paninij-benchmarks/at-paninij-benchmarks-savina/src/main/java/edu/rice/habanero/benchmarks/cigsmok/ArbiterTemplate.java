package edu.rice.habanero.benchmarks.cigsmok;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class ArbiterTemplate {
    @Child Smoker[] smokers = new Smoker[CigaretteSmokerConfig.S];
    Random random = new Random(CigaretteSmokerConfig.R * CigaretteSmokerConfig.S);
    int roundsSoFar = 0;

    public void start() {
        notifyRandomSmoker();
    }

    public void design(Arbiter self ) {
        for (Smoker s : smokers) s.wire(self);
    }

    public void notifySmoking() {
        roundsSoFar++;
        if (roundsSoFar >= CigaretteSmokerConfig.R) {
            for (Smoker s : smokers) s.exit();
        } else {
            notifyRandomSmoker();
        }
    }

    private void notifyRandomSmoker() {
        int newSmokerIndex = Math.abs(random.nextInt()) % CigaretteSmokerConfig.S;
        int busyWaitPeriod = random.nextInt(1000) + 10;
        smokers[newSmokerIndex].smoke(busyWaitPeriod);
    }
}
