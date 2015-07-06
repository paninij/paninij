package edu.rice.habanero.benchmarks.trapezoid;

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;

import edu.rice.habanero.benchmarks.trapezoid.TrapezoidalConfig;

@Capsule
public class WorkerTemplate {

    @Future public double process(double left, double right, double partitions) {

        // the sum of areas for this section of partitions
        double area = 0.0;

        int n = (int) ((right - left) / partitions);

        // calculate the `y` values for each partition
        for (int i = 0; i < n; i++) {

            // the left-hand x bound of the partition
            double lx = i * partitions + left;

            // the right-hand x bound of the partition
            double rx = lx + partitions;

            // the `y` value of fx(x) at the left-hand bound
            double ly = TrapezoidalConfig.fx(lx);

            // the `y` value of fx(x) at the right-hand bound
            double ry = TrapezoidalConfig.fx(rx);

            // add to the total area
            area += (ly + ry) * 0.5 * partitions;
        }

        return area;
    }
}
