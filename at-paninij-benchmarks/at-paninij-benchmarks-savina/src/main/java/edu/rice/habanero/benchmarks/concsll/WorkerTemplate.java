package edu.rice.habanero.benchmarks.concsll;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class WorkerTemplate {
    @Wired Master master;
    @Wired SortedList sortedList;
    @Wired int id;

    int writePercent = SortedListConfig.WRITE_PERCENTAGE;
    int sizePercent = SortedListConfig.SIZE_PERCENTAGE;
    int numMessagesPerWorker = SortedListConfig.NUM_MSGS_PER_WORKER;
    int messageCount = 0;
    Random random;

    public void init() {
        random = new Random(id + numMessagesPerWorker + writePercent + sizePercent);
    }

    public void doWork() {
        messageCount++;
        if (messageCount <= numMessagesPerWorker) {
            int anInt = random.nextInt(100);
            if (anInt < sizePercent) {
                sortedList.size(id);
            } else if (anInt < (sizePercent + writePercent)) {
                sortedList.write(random.nextInt(), id);
            } else {
                sortedList.contains(random.nextInt(), id);
            }
        } else {
            master.workerFinished();
        }
    }
}
