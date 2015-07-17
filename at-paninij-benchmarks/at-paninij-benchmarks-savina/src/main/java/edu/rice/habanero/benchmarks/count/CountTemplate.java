package edu.rice.habanero.benchmarks.count;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class CountTemplate {
    @Child Counter c;

    public void run() {
        for (int i = 0; i < CountingConfig.N; i++)
            c.increment();

        int result = c.result();

        if (result != CountingConfig.N) {
            System.out.println("ERROR: expected: " + CountingConfig.N + ", found: " + result);
        } else {
            System.out.println("SUCCESS! received: " + result);
        }
    }

}
