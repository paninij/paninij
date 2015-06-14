package org.paninij.apt.test;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule
public class FooTemplate
{
    @Wired Bar bar;
    int count;
    
    void init() {
        count = 0;
    }
    
    public Integer wiredBarCount() {
        System.err.println("wiredBarCount()");
        return bar.barCount();
    }
    
    public Integer fooCount() {
        System.err.println("fooCount(): " + count);
        return count++;
    }
}
