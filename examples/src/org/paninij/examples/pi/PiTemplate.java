package org.paninij.examples.pi;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class PiTemplate
{
    @Child Worker[] workers = new Worker[10];

    public void run() {
        int ss = 10000;
        double startTime = System.currentTimeMillis();

        Number[] results = new Number[10];
        double total = 0;

        for (int i = 0; i < 10; i++) {
            results[i] = workers[i].compute(ss/workers.length);
            total += results[i].value();
        }

        double pi = 4.0 * total / ss;
        System.out.println("Pi : " + pi);
        double endTime = System.currentTimeMillis();
        System.out.println("Time to compute Pi using " + ss + " samples was: " + (endTime - startTime) + "ms.");
    }
}
