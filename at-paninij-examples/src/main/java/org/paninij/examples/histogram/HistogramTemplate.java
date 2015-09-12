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

/***
 * Classic Histogram problem using the Panini language
 */
package org.paninij.examples.histogram;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class HistogramTemplate
{
    @Local Printer printer;
    @Local Reader reader;
    @Local Bucket[] buckets = new Bucket[128];

    String[] filenames;

    public void init() {
        filenames = new String[1];
        filenames[0] = "shaks12.txt";
    }

    public void design(Histogram self) {
        reader.imports(buckets);
        for (Bucket bucket : buckets) bucket.imports(printer);
    }

    public void run() {
        reader.read(filenames);
    }
}
