package it2;

import org.paninij.runtime.check.Ownership;

import static java.lang.System.out;
import static org.paninij.agent.util.Assert.assertOwnershipError;

public class IT {
    public static void main(String[] args) {
        out.println("Hello, from a `main()` method.");
        Object o = new Object();
        assertOwnershipError(() -> {
            Ownership.move(o, null, o);
        });
    }
}