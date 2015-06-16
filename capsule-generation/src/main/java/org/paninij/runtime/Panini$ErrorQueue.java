package org.paninij.runtime;

import java.util.concurrent.ArrayBlockingQueue;

@SuppressWarnings("serial")
public class Panini$ErrorQueue extends ArrayBlockingQueue<Throwable>
{
    public Panini$ErrorQueue(int capacity) {
        super(capacity);
    }
}
