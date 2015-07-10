package edu.rice.habanero.benchmarks.logmap;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class MasterTemplate {
    @Child RateComputer[] computers = new RateComputer[LogisticMapConfig.numSeries];
    @Child SeriesWorker[] workers = new SeriesWorker[LogisticMapConfig.numSeries];


    FlagFuture flag = new FlagFuture();

    int numWorkRequested = 0;
    int numWorkRecieved = 0;
    double termsSum = 0;

    public void init() {
        for (int i = 0; i < LogisticMapConfig.numSeries; i++) {
            double rate = LogisticMapConfig.startRate + (i * LogisticMapConfig.increment);
            computers[i].initialize(rate);
            workers[i].initialize(i, rate);
        }
    }

    public void design(Master self) {
        for (int i = 0; i < LogisticMapConfig.numSeries; i++) {
            workers[i].wire(self, computers[i]);
        }
    }


    public FlagFuture start() {
        numWorkRequested = LogisticMapConfig.numSeries;
        for (SeriesWorker w : workers) w.getTerm();
        return flag;
    }

}
