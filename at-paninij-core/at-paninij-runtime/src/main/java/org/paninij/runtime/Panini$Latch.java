package org.paninij.runtime;

import java.util.concurrent.CountDownLatch;

public class Panini$Latch {
    private CountDownLatch latch;
    private final Object lock = new Object();

    public Panini$Latch() {
        this.latch = new CountDownLatch(0);
    }

    public Panini$Latch(int count) {
        this.latch = new CountDownLatch(count);
    }

    public void await() throws InterruptedException {
        synchronized(lock) {
            while (latch.getCount() != 0) lock.wait();
        }
    }

    public void countDown() throws InterruptedException {
        synchronized(lock) {
            latch.countDown();
            lock.notifyAll();
        }
    }

    public void countUp() throws InterruptedException {
        synchronized(lock) {
            latch = new CountDownLatch((int) latch.getCount() + 1);
            lock.notifyAll();
        }
    }

    public int getCount() {
        synchronized(lock) {
            return (int) latch.getCount();
        }
    }
}
