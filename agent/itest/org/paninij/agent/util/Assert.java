package org.paninij.agent.util;

import org.paninij.runtime.check.OwnershipMoveError;

/**
 * @author dwtj
 */
public class Assert {
    public static void assertOwnershipError(Runnable r) {
        try {
            r.run();
        } catch (OwnershipMoveError err) {
            return;
        }
        assert false: "Expected an ownership move error to occur.";
    }
}
