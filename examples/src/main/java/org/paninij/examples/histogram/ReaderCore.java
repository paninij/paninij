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
package org.paninij.examples.histogram;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class ReaderTemplate
{
    @Imports Bucket[] buckets;

    public void read(String[] filenames) {
        if (filenames.length == 0) process("shaks12.txt");

        for (String filename : filenames) {
            process(filename);
        }
    }

    private void process(String filename) {
        try (FileInputStream stream = new FileInputStream(new File(filename))) {
            System.out.println("READER: input file " + filename + " successfully opened. Starting processing...");
            int r;
            while ((r = stream.read()) != -1) {
                buckets[(char) r].bump();
            }
            System.out.println("READER: reading complete. Asking buckets to print count.");
        } catch (IOException e) {
            System.out.println(e);
        }

        for (int i = 0; i < buckets.length; i++) buckets[i].finish(i);

        System.out.println("READER: work complete.");

    }
}
