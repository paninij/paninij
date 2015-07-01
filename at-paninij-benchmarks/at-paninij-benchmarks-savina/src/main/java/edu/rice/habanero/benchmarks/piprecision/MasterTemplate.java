package edu.rice.habanero.benchmarks.piprecision;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

import edu.rice.habanero.benchmarks.pingpong.FlagFuture;

@Capsule public class MasterTemplate {
    @Child Delegator d;
    @Child Worker[] workers = new Worker[PiPrecisionConfig.NUM_WORKERS];

    public void design(Master self) {
        d.wire(workers);
        for (Worker w : workers) w.wire(d);
    }

    public void run() {
        FlagFuture f = d.start();
        while (!f.isDone()) {
            // wait
        }
    }

}
