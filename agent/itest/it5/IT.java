package it5;

import org.paninij.lang.CapsuleSystem;

import static org.paninij.agent.util.Assert.assertOwnershipError;

/**
 * @author dwtj
 */
public class IT {
    public static void main(String[] args) {
        assertOwnershipError(() -> {
            CapsuleSystem.start(Client.class, null);
        });
    }
}
