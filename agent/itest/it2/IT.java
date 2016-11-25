package it2;

import org.paninij.runtime.check.Ownership;

import static java.lang.System.out;

public class IT {
    public static void main(String[] args) {
        out.println("Hello, from a `main()` method.");
        Object o = new Object();
        try {
            Ownership.move(o, null, o);
        } catch (Error err) {
            return;
        }
        assert false: "Expected an ownership move error to occur.";
    }
}