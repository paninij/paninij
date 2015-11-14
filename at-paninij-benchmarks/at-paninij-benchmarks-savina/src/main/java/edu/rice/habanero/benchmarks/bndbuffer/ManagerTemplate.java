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
package edu.rice.habanero.benchmarks.bndbuffer;

import java.util.LinkedList;
import java.util.Queue;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class ManagerTemplate {

    int numConsumers = ProdConsBoundedBufferConfig.numConsumers;
    int numProducers = ProdConsBoundedBufferConfig.numProducers;
    int numTerminatedProducers = 0;

    @Local Consumer[] consumers = new Consumer[numConsumers];
    @Local Producer[] producers = new Producer[numProducers];

    Queue<Double> availableData = new LinkedList<Double>();
    Queue<Consumer> availableConsumers = new LinkedList<Consumer>();
    Queue<Producer> availableProducers = new LinkedList<Producer>();

    int adjustedBufferSize = ProdConsBoundedBufferConfig.bufferSize - numProducers;

    public void design(Manager self) {
        for (int i = 0; i < consumers.length; i++) {
            consumers[i].imports(self, i);
            availableConsumers.add(consumers[i]);
        }
        for (int i = 0; i < producers.length; i++) {
            producers[i].imports(self, i);
        }
    }

    public void start() {
        for (Producer p : producers) p.produce();
    }

    public void dataProduced(int id, double data) {

        if (availableConsumers.isEmpty()) {
            availableData.add(data);
        } else {
            availableConsumers.poll().consume(data);
        }

        if (availableData.size() >= adjustedBufferSize) {
            availableProducers.add(producers[id]);
        } else {
            producers[id].produce();
        }

    }

    public void dataConsumed(int id) {

        if (availableData.isEmpty()) {
            availableConsumers.add(consumers[id]);
            tryExit();
        } else {
            consumers[id].consume(availableData.poll());

            if (!availableProducers.isEmpty()) {
                availableProducers.poll().produce();
            }
        }
    }

    public void producerFinished() {
        numTerminatedProducers++;
        tryExit();
    }

    private void tryExit() {
        if (numTerminatedProducers == numProducers && availableConsumers.size() == numConsumers) {
            for (Consumer c : consumers) c.exit();
            for (Producer p : producers) p.exit();
        }
    }

}
