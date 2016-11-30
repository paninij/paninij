package it3;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;
import org.paninij.lang.Root;

import static java.lang.System.out;

@Capsule class ReceiverTemplate {
    public void receive(Object o) {
        out.println(o);
    }
}
