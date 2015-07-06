/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, Hridesh Rajan
 */
package org.paninij.examples.pi;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

/***
 * Calculation of Pi using the Panini language
 *
 * This computation uses the Monte Carlo Method.
 */
@Capsule
public class PiTemplate
{
    // how many samples to run for computing pi
    static int SAMPLE_SIZE = 100000;

    // how many worker capsules
    static int WORKER_COUNT = 10;

    // an array of worker capsules
    @Child Worker[] workers = new Worker[WORKER_COUNT];

    public void run() {
        Number[] results = new Number[WORKER_COUNT];

        double total = 0;
        double partition = SAMPLE_SIZE/WORKER_COUNT;


        for (int i = 0; i < WORKER_COUNT; i++)
            results[i] = workers[i].compute(partition);

        for (Number result : results)
            total += result.value();


        double pi = 4.0 * total / SAMPLE_SIZE;

        System.out.println("Estimate for pi using " + SAMPLE_SIZE + " samples: " + pi);
    }
}
