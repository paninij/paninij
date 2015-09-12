package edu.rice.habanero.benchmarks.bndbuffer;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class ConsumerTemplate {

    @Imports Manager manager;
    @Imports int id;

    double consItem = 0;

    public void consume(Double data) {
        consItem = ProdConsBoundedBufferConfig.processItem(consItem + data, ProdConsBoundedBufferConfig.consCost);
        manager.dataConsumed(id);
    }

}
