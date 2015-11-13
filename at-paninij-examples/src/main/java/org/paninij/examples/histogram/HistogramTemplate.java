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


/***
 * Classic Histogram problem using the Panini language
 */
package org.paninij.examples.histogram;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class HistogramTemplate
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
    
    public static void main(String[] args) {
        CapsuleSystem.start(Histogram.class, args);
    }
}
