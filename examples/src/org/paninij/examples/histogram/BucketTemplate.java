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
package org.paninij.examples.histogram;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

/**
 * Each Bucket holds the count for an ascii character.
 * Buckets also require a Printer capsule.
 */
@Capsule public class BucketTemplate
{
    @Wired Printer p;

    long count;

    void init() {
        count = 0;
    }

    void bump() {
        count++;
    }

    void finish(int index) {
        p.print("" + index + "(" + (char) index + "):" + count);
    }
}
