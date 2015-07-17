package edu.rice.habanero.benchmarks.concsll;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class MasterTemplate {

    @Child Worker[] workers = new Worker[SortedListConfig.NUM_ENTITIES];
    @Child SortedList sortedList;

    int numWorkersTerminated = 0;
    FlagFuture flag = new FlagFuture();

    public void design(Master self) {
        for (Worker w : workers) w.wire(self, sortedList);
    }

    public FlagFuture start() {
        for (int i = 0; i < SortedListConfig.NUM_ENTITIES; i++) workers[i].doWork();
        return flag;
    }

    public void workerFinished() {
        numWorkersTerminated++;
        if (numWorkersTerminated == SortedListConfig.NUM_ENTITIES) {
            sortedList.printResult();
            flag.resolve();
        }
    }
}
