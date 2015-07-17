package edu.rice.habanero.benchmarks.bndbuffer;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class ConsumerTemplate {

    @Wired Manager manager;

    double consItem = 0;
    int id;

    public void consume(Double data) {
        consItem = ProdConsBoundedBufferConfig.processItem(consItem + data, ProdConsBoundedBufferConfig.consCost);
        manager.dataConsumed(id);
    }

    public void setId(int id) {
        this.id = id;
    }

}
