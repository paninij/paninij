package edu.rice.habanero.benchmarks.concdict;

import java.util.HashMap;
import java.util.Map;

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;

import edu.rice.habanero.benchmarks.BenchmarkRunner;

@Capsule public class DictionaryTemplate {

    Map<Integer, Integer> dataMap = new HashMap<Integer, Integer>(DictionaryConfig.DATA_MAP);

    public void write(int key, int value) {
        dataMap.put(key, value);
    }

    @Future
    public int read(int key) {
        Integer val = dataMap.get(key);
        return val == null ? -1 : val;
    }

    public void printResult() {
        System.out.printf(BenchmarkRunner.argOutputFormat, "Dictionary Size", dataMap.size());
    }

}
