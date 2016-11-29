package it2;

import org.paninij.lang.CapsuleSystem;

import static org.paninij.agent.util.Assert.assertOwnershipError;

public class IT {
    public static void main(String[] args) {
        assertOwnershipError(() -> {
            CapsuleSystem.start(Server.class, null);
        });
    }
}