package org.paninij.benchmarks.savina.util;

public class FlagFuture {
    private boolean resolved;

    public void resolve() {
        synchronized (this) {
            this.resolved = true;
            this.notifyAll();
        }
    }

    public synchronized boolean isDone() {
        return this.resolved;
    }

    public void block()  {
        while (!this.resolved) {
            try {
                synchronized (this) {
                    while (!resolved) this.wait();
                }
            } catch (InterruptedException e) { /* try waiting again */ }
         }
    }

}
