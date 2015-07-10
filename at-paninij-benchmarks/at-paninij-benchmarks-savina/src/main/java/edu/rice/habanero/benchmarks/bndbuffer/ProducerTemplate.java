package edu.rice.habanero.benchmarks.bndbuffer;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class ProducerTemplate {

    @Wired Manager manager;

    double prodItem = 0;
    int itemsProduced = 0;

    public Data produce(int id, int indx) {
        prodItem = ProdConsBoundedBufferConfig.processItem(prodItem, ProdConsBoundedBufferConfig.prodCost);
        itemsProduced++;
        manager.dataProduced(indx);
        return new Data(prodItem, id, indx);
    }

}
