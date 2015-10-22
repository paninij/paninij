package edu.rice.habanero.benchmarks.logmap;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class LogisticMapTemplate
{
    int numTerms = LogisticMapConfig.numTerms;
    int numWorkers = LogisticMapConfig.numSeries;

    @Local SeriesWorker[] workers = new SeriesWorker[numWorkers];

    int numWorkRequested = 0;
    int numWorkRecieved = 0;
    double termsSum = 0;

    public void design(LogisticMap self) {
        for (int i = 0; i < numWorkers; i++) {
            double startTerm = i * LogisticMapConfig.increment;
            workers[i].imports(i, startTerm);
        }
    }

    public void run() {
        for (int i = 0; i < numTerms; i++) {
            for (SeriesWorker w : workers) w.getTerm();
        }

        for (SeriesWorker w : workers) {
            termsSum += w.getResult();
        }

        System.out.println("Terms sum: " + termsSum);
    }
}
