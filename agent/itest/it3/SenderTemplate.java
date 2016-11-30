package it3;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

import static java.lang.System.out;

@Root @Capsule
public class SenderTemplate {

    @Local Receiver receiver;

    public void run() {
        Object obj = new Object();
        helper(obj);
        out.println(obj);  // Use-after-move.
    }

    private void helper(Object obj) {
        receiver.receive(obj);  // Attempted move would cause ownership conflict.
    }
}
