package org.paninij.runtime;

import java.util.concurrent.ConcurrentHashMap;

public class Panini$System
{
    /**
     * System-global variable meant to hold thread-local references to a capsule instance. This
     * makes it possible for a capsule--regardless of the environment or context in which it is
     * executing--can always access itself (i.e. the capsule variable's `this`).
     * 
     * (The initial motivation for this was to enable a capsule to get a reference to itself while
     * it was running the procedure wrapper on another capsule.)
     */
    public static final ThreadLocal<Capsule$Thread> self = new ThreadLocal<Capsule$Thread>();
    
    public static final ConcurrentHashMap<Object, Panini$ErrorQueue> errors
                         = new ConcurrentHashMap<Object, Panini$ErrorQueue>();
}
