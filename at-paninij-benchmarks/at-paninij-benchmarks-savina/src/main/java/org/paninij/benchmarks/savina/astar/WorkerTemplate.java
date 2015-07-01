package org.paninij.benchmarks.savina.astar;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule
public class WorkerTemplate
{
    @Wired Master master;

    int id;
    int threshold;

    public void init() {
        this.id = 0;
        this.threshold = GuidedSearchConfig.THRESHOLD;
    }

    public void setId(int id) {

    }

    public void process(Object msg) {

    }

    private void search(GuidedSearchConfig.WorkMessage wm) {

    }

}
