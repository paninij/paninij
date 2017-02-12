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
package edu.rice.habanero.benchmarks.concsll;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class WorkerCore {
    @Imports Master master;
    @Imports SortedList sortedList;
    @Imports int id;

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
