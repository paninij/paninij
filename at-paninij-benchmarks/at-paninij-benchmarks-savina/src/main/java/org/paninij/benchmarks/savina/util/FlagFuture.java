package org.paninij.benchmarks.savina.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FlagFuture implements Future<Void> {
    private boolean resolved;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void resolve() {
        synchronized (this) {
            this.resolved = true;
            this.notifyAll();
        }
    }

    @Override
    public boolean isCancelled()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public synchronized boolean isDone() {
        return this.resolved;
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException
    {
        while (!this.resolved) {
            try {
                synchronized (this) {
                    while (!resolved) this.wait();
                }
            } catch (InterruptedException e) { /* try waiting again */ }
         }
         return null;
    }

    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
            TimeoutException
    {
        // TODO Auto-generated method stub
        return this.get();
    }

}
