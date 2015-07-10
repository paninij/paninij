package edu.rice.habanero.benchmarks.piprecision;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class PiPrecisionTemplate {
    @Child Delegator d;
    @Child Worker[] workers = new Worker[PiPrecisionConfig.NUM_WORKERS];

    public void design(PiPrecision self) {
        d.wire(workers);
        for (Worker w : workers) w.wire(d);
    }

    public void run() {
        FlagFuture wait = d.start();
        wait.block();
    }

}
