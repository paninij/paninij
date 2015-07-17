package edu.rice.habanero.benchmarks.concdict;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class MasterTemplate {
    @Child Worker[] workers = new Worker[DictionaryConfig.NUM_ENTITIES];
    @Child Dictionary dictionary;

    int numWorkersTerminated = 0;
    FlagFuture flag = new FlagFuture();

    public void design(Master self) {
        for (Worker w : workers) w.wire(self, dictionary);
    }

    public FlagFuture start() {
        for (int i = 0; i < DictionaryConfig.NUM_ENTITIES; i++) workers[i].doWork();
        return flag;
    }

    public void workerFinished() {
        numWorkersTerminated++;
        if (numWorkersTerminated == DictionaryConfig.NUM_ENTITIES) {
            dictionary.printResult();
            flag.resolve();
        }
    }
}
