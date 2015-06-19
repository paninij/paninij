package org.paninij.apt.test;

import org.paninij.lang.CapsuleTest;
import org.paninij.lang.Child;
import org.paninij.lang.Test;

@CapsuleTest
public class BasicCapsuleTemplate
{
    @Child Foo foo;
    @Child Bar bar;
    
    void design(BasicCapsule self) {
        foo.wire(bar);
    }
    
    @Test
    public void testFooCount()
    {
        Integer count = foo.fooCount();
        assert count.intValue() == 1;
    }
    
    @Test
    public void testFooCountAgain()
    {
        Integer count = foo.fooCount();
        assert count.intValue() == 1;
    }

    @Test
    public void testBarCount()
    {
        Integer count = bar.barCount();
        assert count.intValue() == 1;
    }

    @Test
    public void testWiredBarCount()
    {
        Integer count = foo.wiredBarCount();
        assert count.intValue() == 1;
    }
    
    @Test
    public void testMultipleCounts()
    {
        final int ITERATIONS = 10;
        final int EXPECTED  = ITERATIONS;

        Integer count = 0;
        for (int idx = 0; idx < ITERATIONS; idx++) {
            count = foo.wiredBarCount();
        }
        assert count.intValue() == EXPECTED;
    }
}
