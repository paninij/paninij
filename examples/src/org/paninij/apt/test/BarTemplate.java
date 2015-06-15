package org.paninij.apt.test;

import org.paninij.lang.Capsule;

@Capsule
public class BarTemplate
{
    int count;
    
    void init() {
        count = 0;
    }
    
    public Integer barCount() {
        System.err.println("barCount(): " + count);
        return count++;
    }
}
