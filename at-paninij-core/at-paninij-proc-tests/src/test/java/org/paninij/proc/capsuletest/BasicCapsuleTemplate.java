package org.paninij.proc.capsuletest;

import org.paninij.lang.CapsuleTest;
import org.paninij.lang.Local;
import org.paninij.lang.Test;

/*
@CapsuleTest
public class BasicCapsuleTemplate
{
    @Local Foo foo;
    @Local Bar bar;
    
    void design(BasicCapsule self) {
        foo.imports(bar);
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
    public void testImportedBarCount()
    {
        Integer count = foo.importedBarCount();
        assert count.intValue() == 1;
    }
    
    @Test
    public void testMultipleCounts()
    {
        final int ITERATIONS = 10;
        final int EXPECTED  = ITERATIONS;

        Integer count = 0;
        for (int idx = 0; idx < ITERATIONS; idx++) {
            count = foo.importedBarCount();
        }
        assert count.intValue() == EXPECTED;
    }
}

*/