package edu.rice.habanero.benchmarks.bndbuffer;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class ConsumerTemplate {

    @Wired Manager manager;
    @Wired int id;

    double consItem = 0;

    public void consume(Double data) {
        consItem = ProdConsBoundedBufferConfig.processItem(consItem + data, ProdConsBoundedBufferConfig.consCost);
        manager.dataConsumed(id);
    }

}
