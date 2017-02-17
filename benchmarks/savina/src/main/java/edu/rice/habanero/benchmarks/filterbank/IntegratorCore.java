/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package edu.rice.habanero.benchmarks.filterbank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imported;

@Capsule
class IntegratorCore {
    @Imported Combine combine;
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
