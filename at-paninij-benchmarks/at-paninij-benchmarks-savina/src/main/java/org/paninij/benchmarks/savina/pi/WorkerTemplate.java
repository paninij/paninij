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
package org.paninij.benchmarks.savina.pi;

import java.util.Random;

import org.paninij.lang.Capsule;

/**
 * Each Worker capsule computes a fraction of the total number of samples.
 */
@Capsule
public class WorkerTemplate
{
    Random prng;

    public void init() {
        this.prng = new Random();
    }

    public Number compute(double num) {
        Number _circleCount = new Number();
        for (double j = 0; j < num; j++) {
            double x = this.prng.nextDouble();
            double y = this.prng.nextDouble();
            if ((x * x + y * y) < 1) _circleCount.incr();
        }
        return _circleCount;
    }
}
