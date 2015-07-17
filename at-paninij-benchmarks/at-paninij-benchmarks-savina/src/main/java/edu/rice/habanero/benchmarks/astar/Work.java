package edu.rice.habanero.benchmarks.astar;

import edu.rice.habanero.benchmarks.astar.GuidedSearchConfig.GridNode;

public class Work {
    private GridNode node;
    private GridNode target;

    public Work(GridNode node, GridNode target) {
        this.node = node;
        this.target = target;
    }

    public GridNode node() { return node; }
    public GridNode target() { return target; }
}
