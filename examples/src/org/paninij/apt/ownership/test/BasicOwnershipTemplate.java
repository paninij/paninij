package org.paninij.apt.ownership.test;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.paninij.lang.CapsuleTest;
import org.paninij.lang.Child;
import org.paninij.lang.Test;
import org.paninij.runtime.Panini$ErrorQueue;
import org.paninij.runtime.Panini$System;
import org.paninij.runtime.Panini$Capsule;

@CapsuleTest
public class BasicOwnershipTemplate
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
        assertThrowsAssertionError(test, leakyServer);
    }


    /**
     * Attempts to make the client leak a reference to its secret.
     */
    @Test
    void unsafeStateReturn()
    {
        Consumer<Void> test = (_dummy -> leakyServer.getSecret());
        assertThrowsAssertionError(test, leakyServer);
    }
    

    /**
     * Runs the given `fn` and asserts that an assertion error is thrown by this capsule tester.
     */
    private static void assertThrowsAssertionError(Consumer<Void> fn)
    {
        boolean assertion_caught = false;

        try {
            fn.accept(null);
        } catch (AssertionError err) {
            assertion_caught = true;
        }

        if (assertion_caught == false) {
            String msg = "This `Panini$Capsule` was expected to throw an assertion error, but did not.";
            throw new AssertionError(msg);
        }
    }

    
    /**
     * Runs the given `fn` and asserts that an assertion error is thrown over in the other
     * `Panini$Capsule`.
     * 
     * An assertion error will be thrown if an assertion error is not thrown in the other
     * `Panini$Capsule` after about 1 second.
     */
    private static void assertThrowsAssertionError(Consumer<Void> fn, Panini$Capsule cap)
    {
        fn.accept(null);

        Throwable thrown = null;
        try
        {
            Panini$ErrorQueue queue = Panini$System.errors.get(cap);
            thrown = queue.poll(1, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (thrown == null) {
                throw new AssertionError("The other `Panini$Capsule` did not assert.");
            }
        }
    }
}
