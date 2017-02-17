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

import org.paninij.lang.Capsule;
import org.paninij.lang.Imported;

@Capsule class ProducerCore {

    @Imported Manager manager;
    @Imported int id;

    double prodItem = 0;
    int numItemsToProduce = ProdConsBoundedBufferConfig.numItemsPerProducer;
    int itemsProduced = 0;
    boolean done = false;

    void produce() {
        if (itemsProduced == numItemsToProduce && !done) {
            manager.producerFinished();
            done = true;
            return;
        }
        prodItem = ProdConsBoundedBufferConfig.processItem(prodItem, ProdConsBoundedBufferConfig.prodCost);
        itemsProduced++;
        manager.dataProduced(id, prodItem);
    }

}
