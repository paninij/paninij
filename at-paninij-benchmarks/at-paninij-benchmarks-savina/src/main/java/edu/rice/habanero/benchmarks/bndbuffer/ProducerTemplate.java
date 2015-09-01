package edu.rice.habanero.benchmarks.bndbuffer;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class ProducerTemplate {

    @Imports Manager manager;
    @Imports int id;

    double prodItem = 0;
    int numItemsToProduce = ProdConsBoundedBufferConfig.numItemsPerProducer;
    int itemsProduced = 0;
    boolean done = false;

    public void produce() {
        if (itemsProduced == numItemsToProduce && !done) {
            manager.producerFinished();
            done = true;
            return;
        }
        prodItem = ProdConsBoundedBufferConfig.processItem(prodItem, ProdConsBoundedBufferConfig.prodCost);
        itemsProduced++;
        manager.dataProduced(id, prodItem);
    }

}
