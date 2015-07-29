package edu.rice.habanero.benchmarks.trapezoid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

import edu.rice.habanero.benchmarks.trapezoid.TrapezoidalConfig;

@Capsule
public class TrapezoidTemplate
{
    // The array of worker capsules
    @Child Worker[] workers = new Worker[TrapezoidalConfig.W];

    public void run() {
        // we'll populate this list with results from each worker
        List<Future<Double>> results = new ArrayList<Future<Double>>();

        double range = (TrapezoidalConfig.R - TrapezoidalConfig.L) / TrapezoidalConfig.W;
        double precision = (TrapezoidalConfig.R - TrapezoidalConfig.L) / TrapezoidalConfig.N;

        // we will now tell delegate some partitions to each capsule
        for (int i = 0; i < TrapezoidalConfig.W; i++) {

            double left = range * i + TrapezoidalConfig.L;
            double right = left + range;

            // tell the worker to start processing, and add the Future
            // the our list results
            results.add(workers[i].process(left, right, precision));
        }

        // the sum of all of the worker's sums
        double area = 0.0;

        // loop through each result and add it to the total sum
        try {
            for (Future<Double> result : results) area += result.get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        System.out.println("Area: " + area);
    }

}
