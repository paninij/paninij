package edu.rice.habanero.benchmarks.concsll;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

import edu.rice.habanero.benchmarks.BenchmarkRunner;

@Capsule public class SortedListTemplate {
    @Wired Worker[] workers;
    SortedLinkedList<Integer> dataList = new SortedLinkedList<Integer>();

    public void write(int value, int id) {
        dataList.add(value);
        workers[id].doWork();
    }

    public void size(int id) {
        int size = dataList.size();
        workers[id].doWork();
    }

    public void contains(int value, int id) {
        boolean contains = dataList.contains(value);
        workers[id].doWork();
    }

    public void printResult() {
        System.out.printf(BenchmarkRunner.argOutputFormat, "List Size", dataList.size());
        for (Worker w : workers) w.exit();
    }

}
