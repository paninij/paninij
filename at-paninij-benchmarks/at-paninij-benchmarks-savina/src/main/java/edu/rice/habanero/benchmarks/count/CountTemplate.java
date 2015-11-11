package edu.rice.habanero.benchmarks.count;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule public class CountTemplate
{
    @Local Counter c;

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
