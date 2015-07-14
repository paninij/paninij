package edu.rice.habanero.benchmarks.recmatmul;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;


@Capsule public class WorkerTemplate {

    @Wired Master master;

    final int threshhold = MatMulConfig.BLOCK_THRESHOLD;

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

        if (numBlocks > threshhold) {
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

            double[][] A = MatMulConfig.A;
            double[][] B = MatMulConfig.B;

            int endR = srC + dim;
            int endC = scC + dim;

            for (int i = srC; i < endR; i++) {
                for (int j = scC; j < endC; j++) {
                    for (int k = 0; k < dim; k++) {
                        MatMulConfig.C[i][j] += A[i][scA + k] * B[srB + k][j];
                    }
                }
            }
        }

        master.workFinished();
    }

}
