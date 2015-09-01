package edu.rice.habanero.benchmarks.concdict;

import java.util.HashMap;
import java.util.Map;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

import edu.rice.habanero.benchmarks.BenchmarkRunner;

@Capsule public class DictionaryTemplate {
    @Imports Worker[] workers;
    Map<Integer, Integer> dataMap = new HashMap<Integer, Integer>(DictionaryConfig.DATA_MAP);

    public void write(int key, int value, int id) {
        dataMap.put(key, value);
        workers[id].doWork();
    }

    public void read(int key, int id) {
        Integer val = dataMap.get(key);
        val = val == null ? -1 : val;
        workers[id].doWork();
    }

    public void printResult() {
        System.out.printf(BenchmarkRunner.argOutputFormat, "Dictionary Size", dataMap.size());
        for (Worker w : workers) w.exit();
    }

}
