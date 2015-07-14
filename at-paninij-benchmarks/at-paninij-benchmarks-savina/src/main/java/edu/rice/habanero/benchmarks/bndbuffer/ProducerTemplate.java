package edu.rice.habanero.benchmarks.bndbuffer;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class ProducerTemplate {

    @Wired Manager manager;

    double prodItem = 0;
    int numItemsToProduce = ProdConsBoundedBufferConfig.numItemsPerProducer;
    int itemsProduced = 0;
    int id;
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

    public void setId(int id) {
        this.id = id;
    }

}
