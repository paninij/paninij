package org.paninij.apt.capsuletest;

import org.paninij.apt.capsuletest.Bar;
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
        return bar.barCount();
    }
    
    public Integer fooCount() {
        return ++count;
    }
}
