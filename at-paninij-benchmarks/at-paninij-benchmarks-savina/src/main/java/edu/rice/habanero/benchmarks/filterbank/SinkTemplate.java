package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;

@Capsule public class SinkTemplate {
    int printRate = FilterBankConfig.SINK_PRINT_RATE;
    int count = 0;
    int total = 0;
    FlagFuture flag = new FlagFuture();

    public FlagFuture getFlag() {
        return flag;
    }

    public void process(double value) {
        if (FilterBankConfig.debug && (count == 0)) System.out.println("SinkActor: result = " + value);
        count = (count + 1) % printRate;
        total++;
        // TODO add an onExit(), don't hardcode 2050
        if (total == 2050) flag.resolve();
    }

}
