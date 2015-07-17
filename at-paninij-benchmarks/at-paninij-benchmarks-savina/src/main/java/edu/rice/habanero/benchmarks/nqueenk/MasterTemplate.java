package edu.rice.habanero.benchmarks.nqueenk;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class MasterTemplate {

    long solutionsLimit = NQueensConfig.SOLUTIONS_LIMIT;
    int numWorkers = NQueensConfig.NUM_WORKERS;

    @Child Worker[] workers = new Worker[numWorkers];

    int numWorkersTerminated = 0;
    int numWorkSent = 0;
    int numWorkRecieved = 0;
    int messageCounter = 0;
    long resultCounter = 0;

    FlagFuture flag = new FlagFuture();

    public void design(Master self) {
        for (Worker w : workers) w.wire(self);
    }

    public long getResult() {
        return resultCounter;
    }

    public FlagFuture start() {
        int[] inArray = new int[0];
        sendWork(inArray, 0);
        return flag;
    }

    public void workerDone() {
        numWorkRecieved++;
        if (numWorkRecieved == numWorkSent) {
            goalReached();
        }
    }

    public void sendWork(int[] arr, int depth) {
        workers[messageCounter].nqueensKernalPar(arr, depth);
        messageCounter = (messageCounter + 1) % numWorkers;
        numWorkSent++;
    }

    public void result() {
        resultCounter++;
        if (resultCounter == solutionsLimit) {
            goalReached();
        }
    }

    public void terminate() {
        numWorkersTerminated++;
        if (numWorkersTerminated == numWorkers) {
            flag.resolve();
        }
    }

    private void goalReached() {
        for (Worker w : workers) {
            w.terminate();
            w.exit();
        }
    }

}
