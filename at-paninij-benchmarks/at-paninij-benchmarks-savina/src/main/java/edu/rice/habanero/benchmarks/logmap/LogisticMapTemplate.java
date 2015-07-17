package edu.rice.habanero.benchmarks.logmap;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class LogisticMapTemplate {
    int numTerms = LogisticMapConfig.numTerms;
    int numWorkers = LogisticMapConfig.numSeries;

    @Child SeriesWorker[] workers = new SeriesWorker[numWorkers];

    int numWorkRequested = 0;
    int numWorkRecieved = 0;
    double termsSum = 0;

    public void init() {
        for (int i = 0; i < numWorkers; i++) {
            double startTerm = i * LogisticMapConfig.increment;
            workers[i].initialize(i, startTerm);
        }
    }

    public void run() {
        for (int i = 0; i < numTerms; i++) {
            for (SeriesWorker w : workers) w.getTerm();
        }

        for (SeriesWorker w : workers) {
            termsSum += w.getResult();
        }

        for (SeriesWorker w : workers) w.exit();
        System.out.println("Terms sum: " + termsSum);
    }

}
