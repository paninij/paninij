package org.paninij.apt.test;

import org.paninij.lang.CapsuleTester;
import org.paninij.lang.Child;
import org.paninij.lang.Test;

@CapsuleTester
public class BasicCapsuleTester
{
    @Child Foo foo;
    @Child Bar bar;
    
    void design(BasicCapsule$Thread self) {
        foo.wire(bar);
    }
    
    @Test
    void testFooCount()
    {
        Integer count = foo.fooCount();
        assert count.intValue() == 0;
    }
    
    @Test
    void testFooCountAgain()
    {
        Integer count = foo.fooCount();
        assert count.intValue() == 0;
    }

    @Test
    void testBarCount()
    {
        Integer count = bar.barCount();
        assert count.intValue() == 0;
    }

    @Test
    void testWiredBarCount()
    {
        Integer count = foo.wiredBarCount();
        assert count.intValue() == 0;
    }
    
    @Test
    void testMultipleCounts()
    {
        final int ITERATIONS = 10;
        Integer count = 0;
        for (int idx = 0; idx < ITERATIONS; idx++) {
            count = foo.wiredBarCount();
        }
        assert count.intValue() == ITERATIONS;
    }
}
