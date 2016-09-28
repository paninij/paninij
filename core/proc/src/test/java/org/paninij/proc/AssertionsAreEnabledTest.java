package org.paninij.proc;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.paninij.lang.Test;

/**
 * Simply tests that assertions are enabled during testing.
 *
 * @author dwtj
 */
public class AssertionsAreEnabledTest {

    @Rule ExpectedException expectedException = ExpectedException.none();

    @Test public void test() {
        expectedException.expect(AssertionError.class);
        assert false;
    }
}
