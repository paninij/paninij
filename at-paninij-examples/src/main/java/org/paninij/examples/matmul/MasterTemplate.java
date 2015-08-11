package org.paninij.examples.matmul;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;
import org.paninij.lang.Wired;

@Capsule public class MasterTemplate {
    int numWorkers = Config.NUM_WORKERS;

    @Wired MatrixData data;
    @Child Worker[] workers = new Worker[numWorkers];

    int numWorkSent = 0;
    int numWorkCompleted = 0;

    public void design(Master self) {
        for (Worker w : workers) w.wire(self, data);
    }

    public void start() {
        int len = data.dimension();
        int numBlocks = len * len;
        Work work = new Work(0, 0, 0, 0, 0, 0, 0, numBlocks, len);
        generateWork(work);
    }

    public void workFinished() {
        numWorkCompleted++;
        System.out.println(numWorkCompleted + " / " + numWorkSent);
        if (numWorkCompleted == numWorkSent) {
            for (Worker w : workers) w.exit();
            data.print();
        }
    }

    public void generateWork(Work work) {
        numWorkSent++;
        int indx = (work.srC + work.scC) % numWorkers;
        workers[indx].doWork(work);
    }

}
