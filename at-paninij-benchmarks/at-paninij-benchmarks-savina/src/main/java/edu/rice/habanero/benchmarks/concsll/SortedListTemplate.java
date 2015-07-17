package edu.rice.habanero.benchmarks.concsll;

import org.paninij.lang.Capsule;

import edu.rice.habanero.benchmarks.BenchmarkRunner;

@Capsule public class SortedListTemplate {

    SortedLinkedList<Integer> dataList = new SortedLinkedList<Integer>();

    public void write(int value) {
        dataList.add(value);
    }

    public int size() {
        return dataList.size();
    }

    public boolean contains(int value) {
        return dataList.contains(value);
    }

    public void printResult() {
        System.out.printf(BenchmarkRunner.argOutputFormat, "List Size", dataList.size());
    }

}
