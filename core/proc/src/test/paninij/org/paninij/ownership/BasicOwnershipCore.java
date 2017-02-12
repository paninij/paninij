/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
//package org.paninij.ownership;
//
//import java.util.concurrent.TimeUnit;
//
//import org.junit.Assert;
//import org.paninij.lang.CapsuleTest;
//import org.paninij.lang.Local;
//import org.paninij.lang.Test;
//import org.paninij.runtime.Capsule$Thread;
//import org.paninij.runtime.Panini$Capsule;
//
//@CapsuleTest
//public class BasicOwnershipCore
//{
//    @Local LeakyServer leakyServer;
//    Integer testerSecret = 42;
//    
//
//    @Test
//    public void checkAssertionsEnabled()
//    {
//        boolean assert_enabled = false;
//
//        try {
//            assert false;
//        } catch (AssertionError err) {
//            assert_enabled = true;
//        }
//
//        if (assert_enabled == false) {
//            throw new AssertionError("Assertions are not enabled.");
//        }
//    }
//
//
//    /**
//     * Attempts to perform a completely safe procedure invocation.
//     */
//    @Test
//    public void safeInvocation()
//    {
//        leakyServer.giveInteger(new Integer(10));
//    }
//    
//    
//    @Test
//    public void safeReturn()
//    {
//        leakyServer.getInteger();
//        assertNoError(leakyServer);
//    }
//
//
//    /**
//     * Attempts to perform a procedure invocation which is unsafe because it leaks the tester's
//     * state to the `leakyServer`.
//     */
//    @Test
//    public void unsafeInvocation()
//    {
//        Throwable thrown = null;
//
//        try {
//            leakyServer.giveInteger(testerSecret);
//        } catch (AssertionError err) {
//            thrown = err;
//        }
//
//        if (thrown == null) {
//            Assert.fail("Expected to catch an assertion, but none were thrown.");
//        }
//    }
//
//
//    /**
//     * Attempts to get the client to leak a reference to its core instance (i.e. its `this` ref).
//     */
//    @Test
//    public void unsafeInstanceReturn()
//    {
//        leakyServer.getCoreReference();
//        assertError(leakyServer);
//    }
//
//
//    /**
//     * Attempts to make the client leak a reference to its secret.
//     */
//    @Test
//    public void unsafeStateReturn()
//    {
//        leakyServer.getSecret();
//        assertError(leakyServer);
//    }
//    
//
//    /**
//     * Asserts that no errors or exceptions were thrown by the given `cap`. An assertion error will
//     * be thrown if an assertion error is not thrown in the other `Panini$Capsule` within about 1
//     * second.
//     */
//    private static void assertNoError(Panini$Capsule cap)
//    {
//        Throwable thrown = null;
//        thrown = ((Capsule$Thread) cap).panini$pollErrors(1, TimeUnit.SECONDS);
//        if (thrown != null) {
//            throw new AssertionError("The other `Panini$Capsule` did not assert.");
//        }
//    }
//
//    
//    /**
//     * Asserts that an error or exception was thrown by the given `cap`. An assertion error will be
//     * thrown if an assertion error is not thrown in the other `Panini$Capsule` within about 1 sec.
//     */
//    private static void assertError(Panini$Capsule cap)
//    {
//        Throwable thrown = null;
//        thrown = ((Capsule$Thread) cap).panini$pollErrors(1, TimeUnit.SECONDS);
//        if (thrown == null) {
//            throw new AssertionError("The other `Panini$Capsule` did not assert.");
//        }
//    }
//}
