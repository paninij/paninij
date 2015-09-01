package org.paninij.examples.matmul;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class WorkerTemplate {
    @Imports Master master;
    @Imports MatrixData data;

    public void doWork(Work work) {
        int srA = work.srA;
        int scA = work.scA;
        int srB = work.srB;
        int scB = work.scB;
        int srC = work.srC;
        int scC = work.scC;
        int numBlocks = work.numBlocks;
        int dim = work.dim;
        int newPriority = work.priority + 1;

        if (numBlocks > Config.THRESHOLD) {
            int zerDim = 0;
            int newDim = dim / 2;
            int newNumBlocks = numBlocks / 4;

            master.generateWork(new Work(newPriority, srA + zerDim, scA + zerDim, srB + zerDim, scB + zerDim, srC + zerDim, scC + zerDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + zerDim, scA + newDim, srB + newDim, scB + zerDim, srC + zerDim, scC + zerDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + zerDim, scA + zerDim, srB + zerDim, scB + newDim, srC + zerDim, scC + newDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + zerDim, scA + newDim, srB + newDim, scB + newDim, srC + zerDim, scC + newDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + newDim, scA + zerDim, srB + zerDim, scB + zerDim, srC + newDim, scC + zerDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + newDim, scA + newDim, srB + newDim, scB + zerDim, srC + newDim, scC + zerDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + newDim, scA + zerDim, srB + zerDim, scB + newDim, srC + newDim, scC + newDim, newNumBlocks, newDim));
            master.generateWork(new Work(newPriority, srA + newDim, scA + newDim, srB + newDim, scB + newDim, srC + newDim, scC + newDim, newNumBlocks, newDim));
        } else {
            int endR = srC + dim;
            int endC = scC + dim;

            for (int i = srC; i < endR; i++) {
                for (int j = scC; j < endC; j++) {
                    for (int k = 0; k < dim; k++) {
                        double a = data.getA(i, scA + k);
                        double b = data.getB(srB + k, j);
                        data.addC(i, j, a * b);
                    }
                }
            }
        }
        master.workFinished();
    }

}
