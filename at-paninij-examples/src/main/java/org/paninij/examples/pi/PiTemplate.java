
package org.paninij.examples.pi;

import static org.paninij.examples.pi.Config.SAMPLE_SIZE;
import static org.paninij.examples.pi.Config.WORKER_COUNT;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

/***
 * Calculation of Pi using the Panini language
 *
 * This computation uses the Monte Carlo Method.
 */
@Root
@Capsule
public class PiTemplate
{
    // an array of worker capsules
    @Local Worker[] workers = new Worker[WORKER_COUNT];

    public void run() {
        Number[] results = new Number[WORKER_COUNT];

        double total = 0;
        double partition = SAMPLE_SIZE/WORKER_COUNT;


        for (int i = 0; i < WORKER_COUNT; i++)
            results[i] = workers[i].compute(partition);

        for (Number result : results)
            total += result.value();


        double pi = 4.0 * total / SAMPLE_SIZE;

        System.out.println("Estimate for pi using " + SAMPLE_SIZE + " samples: " + pi);
    }
    
    public static void main(String[] args) {
        CapsuleSystem.start(Pi.class, args);
    }
}
