package edu.rice.habanero.benchmarks.apsp;

import java.util.ArrayList;
import java.util.List;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class MasterTemplate {
    long[][] graphData = ApspUtils.graphData();
    int numNodes = ApspConfig.N;
    int blockSize = ApspConfig.B;
    int numBlocksInSingleDim = numNodes / blockSize;
    int numWorkers = numBlocksInSingleDim * numBlocksInSingleDim;
    int numWorkersFinished = 0;
    FlagFuture flag = new FlagFuture();


    // NOTE we do not have 2d arrays of child/wired capsules yet, so cram into one
    @Child Worker[] workers = new Worker[numWorkers];

    public void design(Master self) {
        for (int bi = 0; bi < numBlocksInSingleDim; bi++) {
            for (int bj = 0; bj < numBlocksInSingleDim; bj++) {

                List<Worker> neighbors = new ArrayList<Worker>();

                // add neighbors in same column
                for (int r = 0; r < numBlocksInSingleDim; r++) {
                    if (r != bi) neighbors.add(workers[r * bj]);
                }

                // add neighbors in same row
                for (int c = 0; c < numBlocksInSingleDim; c++) {
                    if (c != bj) neighbors.add(workers[bi * c]);
                }

                Worker[] n = new Worker[neighbors.size()];
                for (int i = 0; i < neighbors.size(); i++) {
                    n[i] = neighbors.get(i);
                }

                workers[bi * bj].wire(self, n);
            }
        }
    }

    public FlagFuture start() {
        for (int i = 0; i < numBlocksInSingleDim; i++) {
            for (int j = 0; j < numBlocksInSingleDim; j++) {
                workers[i * j].initialize(i * numBlocksInSingleDim + j, graphData);
            }
        }

        for (Worker w : workers) w.start();
        return flag;
    }

    public void workerFinished() {
        System.out.println("worker finished");
        numWorkersFinished++;
        if (numWorkersFinished == numWorkers) {
            for (Worker w : workers) w.exit();
            flag.resolve();
        }
    }

}
