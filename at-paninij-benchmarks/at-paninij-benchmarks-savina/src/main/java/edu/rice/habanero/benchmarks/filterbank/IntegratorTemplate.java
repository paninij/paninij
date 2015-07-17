package edu.rice.habanero.benchmarks.filterbank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class IntegratorTemplate {
    @Wired Combine combine;
    int numChannels = FilterBankConfig.NUM_CHANNELS;
    final List<Map<Integer, Double>> data = new ArrayList<Map<Integer, Double>>();

    public void process(int sourceId, double value) {

        boolean processed = false;
        for (int i = 0; i < data.size(); i++) {
            Map<Integer, Double> map = data.get(i);
            if (!map.containsKey(sourceId)) {
                map.put(sourceId, value);
                processed = true;
                break;
            }
        }

        if (!processed) {
            Map<Integer, Double> newMap = new HashMap<Integer, Double>();
            newMap.put(sourceId,  value);
            data.add(newMap);
        }

        Map<Integer, Double> firstMap = data.get(0);
        if (firstMap.size() == numChannels) {
            combine.process(new DoubleCollection(firstMap.values()));
            data.remove(0);
        }
    }
}
