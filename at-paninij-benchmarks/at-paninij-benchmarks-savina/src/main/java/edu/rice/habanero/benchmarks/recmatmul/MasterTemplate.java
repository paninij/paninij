package edu.rice.habanero.benchmarks.recmatmul;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class MasterTemplate {

    final int numWorkers = MatMulConfig.NUM_WORKERS;
    @Child Worker[] workers = new Worker[numWorkers];

    int numWorkSent = 0;
    int numWorkCompleted = 0;

    FlagFuture flag = new FlagFuture();

    public void design(Master self) {
        for (Worker w : workers) w.wire(self);
    }

    public FlagFuture start() {
        int dataLength = MatMulConfig.DATA_LENGTH;
        int numBlocks = MatMulConfig.DATA_LENGTH * MatMulConfig.DATA_LENGTH;
        Work work = new Work(0, 0, 0, 0, 0, 0, 0, numBlocks, dataLength);
        generateWork(work);
        return flag;
    }

    public void workFinished() {
        numWorkCompleted++;
        if (numWorkCompleted == numWorkSent) {
            for (Worker w : workers) w.exit();
            flag.resolve();
        }
    }

    public void generateWork(Work work) {
        int indx = (work.srC + work.scC) % numWorkers;
        workers[indx].doWork(work);
        numWorkSent++;
    }

}
