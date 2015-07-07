package edu.rice.habanero.benchmarks.piprecision;

import java.util.concurrent.ExecutionException;

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
        FlagFuture f = d.start();
        try {
            f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
