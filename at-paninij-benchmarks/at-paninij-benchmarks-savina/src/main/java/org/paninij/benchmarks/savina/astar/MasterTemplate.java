package org.paninij.benchmarks.savina.astar;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule
public class MasterTemplate
{
    @Wired Worker[] workers = new Worker[GuidedSearchConfig.NUM_WORKERS];

    public void begin() {

    }

    public void process(Object msg) {

    }

    private void requestWorkersToStop() {

    }

    private void sendWork(GuidedSearchConfig.WorkMessage wm) {

    }
}
