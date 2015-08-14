package org.paninij.examples.matmul;

public class Work {
    public final int priority;
    public final int srA;
    public final int scA;
    public final int srB;
    public final int scB;
    public final int srC;
    public final int scC;
    public final int numBlocks;
    public final int dim;

    public Work(
            final int priority,
            final int srA, final int scA,
            final int srB, final int scB,
            final int srC, final int scC,
            final int numBlocks, final int dim) {
        this.priority = priority;
        this.srA = srA;
        this.scA = scA;
        this.srB = srB;
        this.scB = scB;
        this.srC = srC;
        this.scC = scC;
        this.numBlocks = numBlocks;
        this.dim = dim;
    }
}
