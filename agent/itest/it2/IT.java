package it2;

import org.paninij.runtime.check.Ownership;

public class IT {
    public static void main(String[] args) {
        System.out.println("Hello, from a `main()` method.");
        Object o = new Object();
        try {
            Ownership.move(o, null, o);
        } catch (Error err) {
            return;
        }
        assert false: "Expected an ownership move error to occur.";
    }
}