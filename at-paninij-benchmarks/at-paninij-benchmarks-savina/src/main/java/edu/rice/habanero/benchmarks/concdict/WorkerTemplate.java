package edu.rice.habanero.benchmarks.concdict;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class WorkerTemplate {

    @Wired Master master;
    @Wired Dictionary dictionary;

    int writePercent = DictionaryConfig.WRITE_PERCENTAGE;
    int work = DictionaryConfig.NUM_MSGS_PER_WORKER;
    Random random = new Random();

    public void doWork() {
        while (work > 0) {
            int anInt = random.nextInt(100);
            if (anInt < writePercent) {
                dictionary.write(Math.abs(random.nextInt()) % DictionaryConfig.DATA_LIMIT, random.nextInt());
            } else {
                dictionary.read(Math.abs(random.nextInt()) % DictionaryConfig.DATA_LIMIT);
            }
            work--;
        }
        master.exit();
        master.workerFinished();
    }

}
