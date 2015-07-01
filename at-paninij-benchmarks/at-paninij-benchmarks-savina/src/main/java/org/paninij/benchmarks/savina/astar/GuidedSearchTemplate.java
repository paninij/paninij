package org.paninij.benchmarks.savina.astar;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class GuidedSearchTemplate
{

    @Child Master master;
    @Child Worker[] workers = new Worker[GuidedSearchConfig.NUM_WORKERS];

    public void design(GuidedSearch self) {
        master.wire(workers);
        for (Worker worker : workers) worker.wire(master);
    }

    public void run() {
        GuidedSearchConfig.initializeData();
        master.begin();
    }
}
