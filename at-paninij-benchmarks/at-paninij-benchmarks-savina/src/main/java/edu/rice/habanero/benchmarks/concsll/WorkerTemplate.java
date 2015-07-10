package edu.rice.habanero.benchmarks.concsll;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class WorkerTemplate {
    @Wired Master master;
    @Wired SortedList sortedList;

    int writePercent = SortedListConfig.WRITE_PERCENTAGE;
    int sizePercent = SortedListConfig.SIZE_PERCENTAGE;
    int work = SortedListConfig.NUM_MSGS_PER_WORKER;
    Random random = new Random();

    public void doWork() {
        while (work > 0) {
            int anInt = random.nextInt(100);
            if (anInt < sizePercent) {
                sortedList.size();
            } else if (anInt < (sizePercent + writePercent)) {
                sortedList.write(random.nextInt());
            } else {
                sortedList.contains(random.nextInt());
            }
            work--;
        }
        master.exit();
        master.workerFinished();
    }
}
