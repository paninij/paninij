package org.paninij.agent.util;

/**
 * @author dwtj
 */
public class Assert {
    public static void assertOwnershipError(Runnable r) {
        try {
            r.run();
        } catch (Error err) {
            return;
        }
        assert false: "Expected an ownership move error to occur.";
    }
}
