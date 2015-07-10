package edu.rice.habanero.benchmarks.bndbuffer;

import java.util.LinkedList;
import java.util.Queue;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class ManagerTemplate {
    @Child Consumer[] consumers = new Consumer[ProdConsBoundedBufferConfig.numConsumers];
    @Child Producer[] producers = new Producer[ProdConsBoundedBufferConfig.numProducers];

    Queue<Double> pendingData = new LinkedList<Double>();
    Queue<Consumer> avaliableConsumers = new LinkedList<Consumer>();
    Queue<Producer> avaliableProducers = new LinkedList<Producer>();

    Data[] results = new Data[ProdConsBoundedBufferConfig.bufferSize];

    public void dataProduced(int indx) {
        Data d = results[indx];
        if (avaliableConsumers.isEmpty()) {
            pendingData.add(d.getValue());
        } else {
        }
    }

}
