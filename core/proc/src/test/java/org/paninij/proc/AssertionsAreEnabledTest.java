package org.paninij.proc;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Simply tests that assertions are enabled during testing.
 *
 * @author dwtj
 */
public class AssertionsAreEnabledTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test() {
        expectedException.expect(AssertionError.class);
        assert false;
    }
}
