package edu.rice.habanero.benchmarks.astar;

import java.util.LinkedList;
import java.util.Queue;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

import edu.rice.habanero.benchmarks.astar.GuidedSearchConfig.GridNode;

@Capsule public class WorkerTemplate {
    @Wired Master master;

    int threshold = GuidedSearchConfig.THRESHOLD;

    public void search(Work work) {

        GridNode target = work.target();
        Queue<GridNode> workQueue = new LinkedList<GridNode>();
        workQueue.add(work.node());

        int nodesProcessed = 0;

        while (!workQueue.isEmpty() && nodesProcessed < threshold) {
            nodesProcessed++;
            GuidedSearchConfig.busyWait();

            GridNode loopNode = workQueue.poll();
            int numNeighbors = loopNode.numNeighbors();

            for (int i = 0; i < numNeighbors; i++) {
                GridNode loopNeighbor = loopNode.neighbor(i);
                boolean success = loopNeighbor.setParent(loopNode);
                if (success) {
                    if (loopNeighbor.equals(target)) {
                        master.goalReached();
                        return;
                    } else {
                        workQueue.add(loopNeighbor);
                    }
                }
            }

            while (!workQueue.isEmpty()) {
                Work w = new Work(workQueue.poll(), target);
                master.sendWork(w);
            }

            master.workComplete();
        }
    }

    public void done() {
        master.workerDone();
    }
}
