package org.paninij.apt.ownership.test;

import java.util.function.Consumer;

import org.paninij.lang.CapsuleTester;
import org.paninij.lang.Child;
import org.paninij.lang.Test;

@CapsuleTester
public class BasicOwnershipTester
{
    @Child LeakyServer leakyServer;
    Integer testerSecret = 42;
    
    /**
     * Attempts to perform a completely safe procedure invocation.
     */
    @Test
    void safeInvocation()
    {
        leakyServer.giveInteger(new Integer(10));
    }
    
    /**
     * Attempts to perform a procedure invocation which is unsafe because it leaks the tester's
     * state to the `leakyServer`.
     */
    @Test
    void unsafeInvocation()
    {
        Consumer<Void> test = (_dummy -> leakyServer.giveInteger(testerSecret));
        assertThrowsAssertionError(test);
    }

    /**
     * Attempts to get the client to leak a reference to its template instance (i.e. its `this` ref).
     */
    @Test
    void unsafeInstanceReturn()
    {
        Consumer<Void> test = (_dummy -> leakyServer.getTemplateReference());

        // TODO: Assert that the ownership assertion is thrown in the other thread!
        test.accept(null);
    }

    /**
     * Attempts to make the client leak a reference to its secret.
     */
    @Test
    void unsafeStateReturn()
    {
        Consumer<Void> test = (_dummy -> leakyServer.getSecret());

        // TODO: Assert that the ownership assertion is thrown in the other thread!
        test.accept(null);
    }
    
    private static void assertThrowsAssertionError(Consumer<Void> fn)
    {
        boolean assertion_caught = false;
        try {
            fn.accept(null);
        } catch (AssertionError err) {
            assertion_caught = true;
        }
        assert assertion_caught;
    }
}
