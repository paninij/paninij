package edu.rice.habanero.benchmarks.concdict;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class WorkerTemplate {

    @Imports Master master;
    @Imports Dictionary dictionary;
    @Imports int id;

    int writePercent = DictionaryConfig.WRITE_PERCENTAGE;
    int numMessagesPerWorker = DictionaryConfig.NUM_MSGS_PER_WORKER;
    int messageCount = 0;
    Random random;

    public void init() {
        random = new Random(id + numMessagesPerWorker + writePercent);
    }

    public void doWork() {
        messageCount++;
        if (messageCount <= numMessagesPerWorker) {
            int anInt = random.nextInt(100);
            if (anInt < writePercent) {
                dictionary.write(Math.abs(random.nextInt()) % DictionaryConfig.DATA_LIMIT, random.nextInt(), id);
            } else {
                dictionary.read(Math.abs(random.nextInt()) % DictionaryConfig.DATA_LIMIT, id);
            }
        } else {
            master.workerFinished();
        }
    }

}
